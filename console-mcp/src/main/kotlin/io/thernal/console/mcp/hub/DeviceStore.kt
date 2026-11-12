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
 */
class DeviceStore(private val recordLimit: Int = DEFAULT_RECORD_LIMIT) {
    private val lock = Any()
    private val records = LinkedHashMap<String, Record>()
    private val _shape = MutableStateFlow(0L)
    private val _recordsVersion = MutableStateFlow(0L)

    /** Changes whenever a device connects, disconnects or sends a new manifest. */
    val shape: StateFlow<Long> = _shape.asStateFlow()

    /** Changes whenever a record arrives. */
    val recordsVersion: StateFlow<Long> = _recordsVersion.asStateFlow()

    fun connect(
        info: HubDeviceInfo,
        address: String,
        link: HubLink,
    ) {
        synchronized(lock) {
            val record = records.getOrPut(info.id) { Record(info) }
            record.info = info
            record.address = address
            record.links += link
        }
        _shape.update { it + 1 }
    }

    fun disconnect(
        deviceId: String,
        link: HubLink,
    ) {
        synchronized(lock) { records[deviceId]?.links?.remove(link) }
        _shape.update { it + 1 }
    }

    fun setManifest(
        deviceId: String,
        manifest: HubManifest,
    ) {
        synchronized(lock) { records[deviceId]?.manifest = manifest }
        _shape.update { it + 1 }
    }

    fun addRecord(
        deviceId: String,
        record: HubRecord,
    ) {
        synchronized(lock) {
            val feed = records[deviceId]?.feeds?.getOrPut(record.feed) { TreeMap() } ?: return
            feed[record.seq] = record
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
        val links = mutableListOf<HubLink>()
        val feeds = LinkedHashMap<String, TreeMap<Long, HubRecord>>()
    }

    companion object {
        const val DEFAULT_RECORD_LIMIT = 20_000
    }
}
