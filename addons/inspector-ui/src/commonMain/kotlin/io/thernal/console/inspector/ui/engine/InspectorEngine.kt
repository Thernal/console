package io.thernal.console.inspector.ui.engine

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.tooling.CompositionData
import androidx.compose.ui.geometry.Offset
import io.thernal.console.inspector.ConsoleInspector
import io.thernal.console.inspector.InspectorCommand
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.TimeSource

/** Single owner of the inspector's runtime state. Everything here runs on the main thread. */
internal object InspectorEngine {
    private const val STATS_INTERVAL_MILLIS = 250L
    private const val FLASH_RETENTION_MILLIS = 10_000L
    private const val MIN_AUTO_REFRESH_GAP_MILLIS = 500L
    private const val BUILD_COST_FACTOR = 8L
    private const val MILLIS_PER_SECOND = 1_000L
    private const val MICROS_PER_MILLI = 1_000L

    /** Slot tables of sub-compositions, filled by Compose while `LocalInspectionTables` is provided. */
    val tables: MutableSet<CompositionData> = mutableSetOf()
    var rootData: CompositionData? = null

    private val clock = TimeSource.Monotonic.markNow()
    private val observer = RecompositionObserver()
    private val tally = RecompositionCounts()
    private val reasons = HashMap<Any, String>()
    private var isCountsDirty = false
    private var isStructureDirty = false
    private var hadAppScopeInPass = false
    private var isTabVisible = false
    private var wasFrameworkHidden = true
    private var nextAutoRefreshMillis = 0L

    private val _snapshot = MutableStateFlow(InspectorSnapshot.Empty)
    val snapshot: StateFlow<InspectorSnapshot> = _snapshot.asStateFlow()

    private val _selectedKey = MutableStateFlow<Any?>(null)
    val selectedKey: StateFlow<Any?> = _selectedKey.asStateFlow()

    private val _inspectMode = MutableStateFlow(false)
    val inspectMode: StateFlow<Boolean> = _inspectMode.asStateFlow()

    private val _isFrozen = MutableStateFlow(false)
    val isFrozen: StateFlow<Boolean> = _isFrozen.asStateFlow()

    private val _timing = MutableStateFlow(InspectorTiming())
    val timing: StateFlow<InspectorTiming> = _timing.asStateFlow()

    private val _nodeCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val nodeCounts: StateFlow<Map<Int, Int>> = _nodeCounts.asStateFlow()

    /** Recompositions of the node's own scopes only: what the composable itself did, nothing credited from below. */
    private val _ownNodeCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val ownNodeCounts: StateFlow<Map<Int, Int>> = _ownNodeCounts.asStateFlow()

    /** Passes where the node's own function was skipped because its parameters had not changed. */
    private val _skippedCounts = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val skippedCounts: StateFlow<Map<Int, Int>> = _skippedCounts.asStateFlow()

    /** Node key to the clock time of its latest recomposition, for the flash highlight. */
    val flashes = mutableStateMapOf<Any, Long>()

    fun nowMillis(): Long {
        return clock.elapsedNow().inWholeMilliseconds
    }

    suspend fun run() {
        return coroutineScope {
            launch { trackObserver() }
            launch { consumeEvents() }
            launch { consumeCommands() }
            launch { autoRefresh() }
            launch { statsLoop() }
        }
    }

    /** Leaving inspect mode drops the pick too, unless the caller is about to show its details. */
    fun setInspectMode(
        isActive: Boolean,
        keepSelection: Boolean = false,
    ) {
        _inspectMode.value = isActive
        if (!isActive) InspectorMeasure.setMeasuring(false)
        if (!isActive && !keepSelection) _selectedKey.value = null
    }

    fun setTabVisible(isVisible: Boolean) {
        isTabVisible = isVisible
    }

    fun setFrozen(isFrozen: Boolean) {
        _isFrozen.value = isFrozen
    }

    fun select(key: Any?) {
        _selectedKey.value = key
    }

    /** Rebuilds the tree. Skipped while frozen unless [force] is set. */
    fun refresh(force: Boolean = false) {
        if (_isFrozen.value && !force) return
        tables.removeAll { it.isEmpty }
        val sources = buildList {
            rootData?.let { add(it) }
            tables.filterTo(this) { it !== rootData }
        }
        val built = TreeBuilder.build(sources, ConsoleInspector.config.value.appSourceTags)
        tally.retireMissing(_snapshot.value, built)
        tally.markSeen(built.identities())
        _snapshot.value = built
        isCountsDirty = true
        isStructureDirty = false
        val gap = maxOf(MIN_AUTO_REFRESH_GAP_MILLIS, built.buildMicros * BUILD_COST_FACTOR / MICROS_PER_MILLI)
        nextAutoRefreshMillis = nowMillis() + gap
        publishCounts()
    }

    /** Selects the smallest visible node whose bounds contain [point] (window coordinates). */
    fun selectAt(
        point: Offset,
        isPress: Boolean = true,
    ): InspectorNode? {
        refresh(force = true)
        // Picking reaches every component, Text and Icon included: the framework filter only thins out the tree.
        val snapshot = _snapshot.value
        val bounds = snapshot.liveBounds()
        val hit = snapshot.visibleNodes(hideFramework = false)
            .mapNotNull { node ->
                val content = bounds[node.id] ?: return@mapNotNull null
                val area = pickRect(content, point) { node.primaryLayout?.let(::outerBoundsOf) }
                if (area.contains(point)) node to area else null
            }
            .minWithOrNull(compareBy({ it.second.width * it.second.height }, { -it.first.depth }))
            ?.first
        if (InspectorMeasure.isMeasuring.value) {
            val routed = InspectorMeasure.route(hit?.key, isPress)
            if (routed !== InspectorMeasure.KEEP) _selectedKey.value = routed
        } else {
            _selectedKey.value = hit?.key
        }
        return hit
    }

    fun reasonOf(key: Any): String? {
        return reasons[key]
    }

    fun resetStats() {
        tally.reset()
        reasons.clear()
        flashes.clear()
        _timing.value = InspectorTiming()
        _nodeCounts.value = emptyMap()
        _ownNodeCounts.value = emptyMap()
        _skippedCounts.value = emptyMap()
        tally.markSeen(_snapshot.value.identities())
    }

    private suspend fun trackObserver() {
        ConsoleInspector.config
            .map { it.enabled && it.trackRecompositions }
            .distinctUntilChanged()
            .collectLatest { isTracking ->
                if (isTracking) {
                    observer.attach()
                    refresh(force = true)
                } else {
                    observer.detach()
                }
            }
    }

    private suspend fun consumeEvents() {
        for (event in observer.events) {
            when (event) {
                is InspectorEvent.ScopePassed -> onScopePassed(event.identity, event.wasSkipped)
                is InspectorEvent.ScopeInvalidated -> onScopeInvalidated(event.identity, event.reason)
                is InspectorEvent.CompositionTimed -> onCompositionTimed(event.micros)
            }
        }
    }

    /**
     * Only passes that ran app scopes are timed. The inspector tab recomposes whenever its own numbers
     * change, so counting those passes would feed itself forever.
     */
    private fun onCompositionTimed(micros: Long) {
        if (hadAppScopeInPass) _timing.update { it.plus(micros) }
        hadAppScopeInPass = false
    }

    /**
     * A scope finished a pass. Its first pass is its initial composition. A skipped pass re-entered the scope without
     * running the function, so it is kept apart and never flashes; when skips cannot be read every pass counts.
     */
    private fun onScopePassed(
        identity: Any,
        wasSkipped: Boolean?,
    ) {
        val snapshot = _snapshot.value
        val owner = snapshot.nodeIdForIdentity(identity)?.let { snapshot.nodes.getOrNull(it) }
        if (owner != null && snapshot.isInspectorOwn(owner)) return
        if (owner?.isUnderApp == true) hadAppScopeInPass = true
        val isFirstSight = tally.isFirstSight(identity)
        // A scope the snapshot cannot know is new structure only the first time it shows up. Console's own UI and the
        // tab's lists recompose such scopes all the time; counting every pass as a change rebuilt the tree in a loop.
        if (isFirstSight && !isTabVisible && !snapshot.knowsIdentity(identity)) isStructureDirty = true
        if (isFirstSight) return
        if (wasSkipped == true) {
            tally.skip(identity)
            isCountsDirty = true
            return
        }
        tally.increment(identity)
        isCountsDirty = true
        val node = attributedNode(identity)
        if (node != null && ConsoleInspector.config.value.highlightRecompositions) {
            flashes[node.key] = nowMillis()
        }
    }

    private fun onScopeInvalidated(
        identity: Any,
        reason: String,
    ) {
        reasons[attributedNode(identity)?.key ?: identity] = reason
    }

    /** The node a scope's recompositions are credited to: hidden framework nodes pass them up to the app. */
    private fun attributedNode(identity: Any): InspectorNode? {
        val snapshot = _snapshot.value
        return snapshot.creditedNode(snapshot.ownerOf(identity), ConsoleInspector.config.value.hideFrameworkNodes)
    }

    private suspend fun consumeCommands() {
        ConsoleInspector.commands.collect { command ->
            when (command) {
                InspectorCommand.ResetStats -> resetStats()
                InspectorCommand.Refresh -> refresh(force = true)
                InspectorCommand.ClearSelection -> select(null)
            }
        }
    }

    private suspend fun autoRefresh() {
        ConsoleInspector.config.map { it.enabled to it.autoRefreshSeconds }.distinctUntilChanged()
            .collectLatest { (isEnabled, seconds) ->
                if (!isEnabled || seconds <= 0) return@collectLatest
                while (true) {
                    delay(seconds * MILLIS_PER_SECOND)
                    refresh()
                }
            }
    }

    private suspend fun statsLoop() {
        while (true) {
            delay(STATS_INTERVAL_MILLIS)
            val isFrameworkHidden = ConsoleInspector.config.value.hideFrameworkNodes
            if (isFrameworkHidden != wasFrameworkHidden) {
                wasFrameworkHidden = isFrameworkHidden
                isCountsDirty = true
            }
            if (isCountsDirty) publishCounts()
            if (isStructureDirty && nowMillis() >= nextAutoRefreshMillis) refresh()
            val cutoff = nowMillis() - FLASH_RETENTION_MILLIS
            flashes.entries.removeAll { it.value < cutoff }
        }
    }

    private fun publishCounts() {
        isCountsDirty = false
        // The own counts go first: the details view recomputes when the credited ones change and reads both.
        _skippedCounts.value = tally.byNode(_snapshot.value, hideFramework = false, skipped = true)
        _ownNodeCounts.value = tally.byNode(_snapshot.value, hideFramework = false)
        _nodeCounts.value = tally.byNode(_snapshot.value, ConsoleInspector.config.value.hideFrameworkNodes)
    }
}
