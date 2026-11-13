package io.thernal.console.mcp.hub

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.util.TreeMap

/**
 * Devices the hub has seen, what each shares, and the feed records they streamed. Records are keyed by the app's
 * sequence number, so the backlog an app replays after a reconnect does not duplicate them, and they stay after the
 * device disconnects, which keeps what was written just before a crash readable.
 *
 * An app that restarts keeps its device id but starts a new run, whose sequence numbers start again at 0. The hub
 * shifts each run past the previous one and puts a restart record in every feed between them, so one feed reads
 * across the crash and an agent sees where it happened.
 */
class DeviceStore(
    private val recordLimit: Int = DEFAULT_RECORD_LIMIT,
    private val clock: () -> Long = System::currentTimeMillis,
) {
    private val lock = Any()
    private val records = LinkedHashMap<String, Record>()
    private val _shape = MutableStateFlow(0L)
    private val _recordsVersion = MutableStateFlow(0L)

    /** Changes whenever a device connects, disconnects or sends a new manifest. */
    val shape: StateFlow<Long> = _shape.asStateFlow()

    /** Changes whenever a record arrives. */
    val recordsVersion: StateFlow<Long> = _recordsVersion.asStateFlow()

    /**
     * Adds [link] as a connection of the device. An app that speaks a [protocol] the hub does not read is listed with
     * the reason, but what it sends is ignored, so the agent gets no tools the hub would misread.
     */
    fun connect(
        info: HubDeviceInfo,
        address: String,
        link: HubLink,
        protocol: Int? = HubProtocol.CURRENT,
    ) {
        synchronized(lock) {
            val record = records.getOrPut(info.id) { Record(info) }
            record.info = info
            record.address = address
            record.protocol = protocol
            if (!record.isReadable) record.manifest = HubManifest()
            record.links += link
            val run = info.run ?: info.id
            record.linkRuns[link] = run
            if (run !in record.runBases) record.startRun(run = run, timeMillis = clock())
        }
        _shape.update { it + 1 }
    }

    fun disconnect(
        deviceId: String,
        link: HubLink,
    ) {
        synchronized(lock) {
            records[deviceId]?.let { record ->
                record.links.remove(link)
                record.linkRuns.remove(link)
            }
        }
        _shape.update { it + 1 }
    }

    fun setManifest(
        deviceId: String,
        manifest: HubManifest,
    ) {
        synchronized(lock) {
            val target = records[deviceId]?.takeIf { it.isReadable } ?: return
            target.manifest = manifest
        }
        _shape.update { it + 1 }
    }

    /** Adds a record that arrived over [link]; its sequence number is shifted by the base of the link's run. */
    fun addRecord(
        deviceId: String,
        link: HubLink,
        record: HubRecord,
    ) {
        synchronized(lock) {
            val target = records[deviceId]?.takeIf { it.isReadable } ?: return
            val base = target.linkRuns[link]?.let { target.runBases[it] } ?: 0L
            val seq = base + record.seq
            val feed = target.feeds.getOrPut(record.feed) { TreeMap() }
            feed[seq] = record.copy(seq = seq)
            target.lastSeq = maxOf(target.lastSeq, seq)
            while (feed.size > recordLimit) {
                feed.pollFirstEntry()
            }
        }
        _recordsVersion.update { it + 1 }
    }

    fun devices(): List<HubDevice> {
        return synchronized(lock) {
            records.values.map { record ->
                HubDevice(
                    info = record.info,
                    connected = record.links.isNotEmpty(),
                    address = record.address,
                    manifest = record.manifest,
                    recordCounts = record.feeds.mapValues { it.value.size },
                    runs = record.runBases.size,
                    problem = HubProtocol.problem(record.protocol),
                )
            }
        }
    }

    fun records(
        deviceId: String,
        feed: String,
    ): List<HubRecord> {
        return synchronized(lock) { records[deviceId]?.feeds?.get(feed)?.values?.toList().orEmpty() }
    }

    /** The newest connection of [deviceId], used to send it calls. */
    fun link(deviceId: String): HubLink? {
        return synchronized(lock) { records[deviceId]?.links?.lastOrNull() }
    }

    /** Suspends until some device has sent a manifest. */
    suspend fun awaitFirstManifest() {
        shape.first { devices().any { it.manifest.sections.isNotEmpty() } }
    }

    private class Record(var info: HubDeviceInfo) {
        var address: String = ""
        var manifest: HubManifest = HubManifest()
        var protocol: Int? = HubProtocol.CURRENT
        val isReadable: Boolean get() = HubProtocol.problem(protocol) == null
        val links = mutableListOf<HubLink>()
        val feeds = LinkedHashMap<String, TreeMap<Long, HubRecord>>()

        /** The first hub sequence number of each run, by run id; a run's app sequence numbers are added to it. */
        val runBases = LinkedHashMap<String, Long>()
        val linkRuns = HashMap<HubLink, String>()
        var lastSeq = -1L

        /** Starts [run] after everything recorded so far, with a restart record in each feed when it is not the first. */
        fun startRun(
            run: String,
            timeMillis: Long,
        ) {
            if (runBases.isNotEmpty()) {
                val markerSeq = lastSeq + 1
                val number = runBases.size + 1
                feeds.forEach { (name, feed) ->
                    feed[markerSeq] = HubRecord(
                        feed = name,
                        seq = markerSeq,
                        id = "$RESTART_ID_PREFIX$number",
                        time = timeMillis,
                        summary = "App restarted: run $number starts here, everything above is from the previous run",
                        level = RESTART_LEVEL,
                        tag = RESTART_TAG,
                        kind = RESTART_KIND,
                    )
                }
                lastSeq = markerSeq
            }
            runBases[run] = lastSeq + 1
        }
    }

    companion object {
        const val DEFAULT_RECORD_LIMIT = 20_000
        const val RESTART_KIND = "Restart"
        private const val RESTART_ID_PREFIX = "restart-"
        private const val RESTART_LEVEL = "Info"
        private const val RESTART_TAG = "console"
    }
}
