@file:OptIn(ExperimentalComposeRuntimeApi::class, ComposeToolingApi::class)

package io.thernal.console.inspector.ui.engine

import androidx.compose.runtime.ExperimentalComposeRuntimeApi
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.tooling.ComposeToolingApi
import androidx.compose.runtime.tooling.CompositionObserver
import androidx.compose.runtime.tooling.CompositionObserverHandle
import androidx.compose.runtime.tooling.CompositionRegistrationObserver
import androidx.compose.runtime.tooling.IdentifiableRecomposeScope
import androidx.compose.runtime.tooling.ObservableComposition
import kotlinx.coroutines.channels.Channel
import kotlin.time.TimeMark
import kotlin.time.TimeSource

/**
 * Observes every running Recomposer. Callbacks may arrive on any composition thread, so they only
 * enqueue events; counting happens where the events are consumed.
 */
internal class RecompositionObserver {
    val events = Channel<InspectorEvent>(Channel.UNLIMITED)

    private val marks = mutableMapOf<Any, TimeMark>()
    private var handles: List<CompositionObserverHandle?> = emptyList()

    private val scopeObserver = object : CompositionObserver {
        override fun onBeginComposition(composition: ObservableComposition) {
            marks[composition] = TimeSource.Monotonic.markNow()
        }

        override fun onScopeEnter(scope: RecomposeScope) = Unit

        override fun onReadInScope(
            scope: RecomposeScope,
            value: Any,
        ) = Unit

        override fun onScopeExit(scope: RecomposeScope) {
            events.trySend(InspectorEvent.ScopePassed(keyOf(scope), wasSkipped(scope)))
        }

        override fun onEndComposition(composition: ObservableComposition) {
            marks.remove(composition)?.let {
                events.trySend(InspectorEvent.CompositionTimed(it.elapsedNow().inWholeMicroseconds))
            }
        }

        override fun onScopeInvalidated(
            scope: RecomposeScope,
            value: Any?,
        ) {
            val identity = keyOf(scope)
            val reason = value?.let { it::class.simpleName ?: it.toString() } ?: REASON_INVALIDATE
            events.trySend(InspectorEvent.ScopeInvalidated(identity, reason))
        }

        override fun onScopeDisposed(scope: RecomposeScope) = Unit
    }

    private val registrationObserver = object : CompositionRegistrationObserver {
        override fun onCompositionRegistered(composition: ObservableComposition) {
            composition.setObserver(scopeObserver)
        }

        override fun onCompositionUnregistered(composition: ObservableComposition) = Unit
    }

    fun attach() {
        detach()
        handles = Recomposer.runningRecomposers.value.map { it.observe(registrationObserver) }
    }

    fun detach() {
        handles.forEach { it?.dispose() }
        handles = emptyList()
        marks.clear()
    }

    /** The scope's group identity, or the scope itself when it has none: either maps to a node in the tree. */
    private fun keyOf(scope: RecomposeScope): Any {
        return (scope as? IdentifiableRecomposeScope)?.identity ?: scope
    }

    private companion object {
        const val REASON_INVALIDATE = "invalidate()"
    }
}
