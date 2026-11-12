package io.thernal.console.mcp.hub

import io.ktor.utils.io.ByteChannel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DeviceStoreTest {
    private val device = HubDeviceInfo(id = "d1", name = "Pixel", platform = "Android", os = "16")

    @Test
    fun `a replayed backlog does not duplicate records`() {
        val store = DeviceStore()
        val link = link()
        store.connect(info = device, address = "a", link = link)
        for (seq in 0L..2L) store.addRecord(deviceId = "d1", link = link, record = record(seq))

        for (seq in 0L..3L) store.addRecord(deviceId = "d1", link = link, record = record(seq))

        assertEquals(listOf(0L, 1L, 2L, 3L), store.records("d1", "logs").map { it.seq })
    }

    @Test
    fun `records stay readable after the device disconnects`() {
        val store = DeviceStore()
        val link = link()
        store.connect(info = device, address = "a", link = link)
        store.addRecord(deviceId = "d1", link = link, record = record(0))

        store.disconnect(deviceId = "d1", link = link)

        assertFalse(store.devices().single().connected)
        assertEquals(1, store.records("d1", "logs").size)
        assertTrue(store.devices().single().hasFeed("logs"))
    }

    @Test
    fun `the oldest records go once the limit is reached`() {
        val store = DeviceStore(recordLimit = 2)
        val link = link()
        store.connect(info = device, address = "a", link = link)

        for (seq in 0L..4L) store.addRecord(deviceId = "d1", link = link, record = record(seq))

        assertEquals(listOf(3L, 4L), store.records("d1", "logs").map { it.seq })
    }

    @Test
    fun `a second connection keeps the device connected until both close`() {
        val store = DeviceStore()
        val usb = link()
        store.connect(info = device, address = "usb", link = usb)
        store.connect(info = device, address = "wifi", link = link())

        store.disconnect(deviceId = "d1", link = usb)

        assertTrue(store.devices().single().connected)
    }

    @Test
    fun `a manifest tells which tools a device offers`() {
        val store = DeviceStore()
        store.connect(info = device, address = "a", link = link())
        val tool = HubManifest.Tool(name = "crash_sessions", description = "Crashes")

        store.setManifest(
            deviceId = "d1",
            manifest = HubManifest(
                sections = listOf(HubManifest.Section(id = "c", title = "C", about = "C", tools = listOf(tool))),
            ),
        )

        assertTrue(store.devices().single().hasTool("crash_sessions"))
    }

    @Test
    fun `a restarted app continues its feed after a restart record`() {
        val store = DeviceStore(clock = { RESTART_TIME })
        val first = link()
        store.connect(info = device.copy(run = "r1"), address = "a", link = first)
        for (seq in 0L..1L) store.addRecord(deviceId = "d1", link = first, record = record(seq))
        store.disconnect(deviceId = "d1", link = first)

        val second = link()
        store.connect(info = device.copy(run = "r2"), address = "a", link = second)
        for (seq in 0L..1L) store.addRecord(deviceId = "d1", link = second, record = record(seq))

        val logs = store.records("d1", "logs")
        assertEquals(listOf(0L, 1L, 2L, 3L, 4L), logs.map { it.seq })
        assertEquals(listOf("l0", "l1", "restart-2", "l0", "l1"), logs.map { it.id })
        assertEquals(DeviceStore.RESTART_KIND, logs[2].kind)
        assertEquals(2, store.devices().single().runs)
    }

    @Test
    fun `a reconnect of the same run keeps its sequence numbers`() {
        val store = DeviceStore()
        val first = link()
        store.connect(info = device.copy(run = "r1"), address = "a", link = first)
        store.addRecord(deviceId = "d1", link = first, record = record(0))
        store.disconnect(deviceId = "d1", link = first)

        val again = link()
        store.connect(info = device.copy(run = "r1"), address = "a", link = again)
        for (seq in 0L..1L) store.addRecord(deviceId = "d1", link = again, record = record(seq))

        assertEquals(listOf(0L, 1L), store.records("d1", "logs").map { it.seq })
        assertEquals(1, store.devices().single().runs)
    }

    private fun link(): HubLink {
        return HubLink(ByteChannel())
    }

    private fun record(seq: Long): HubRecord {
        return HubRecord(feed = "logs", seq = seq, id = "l$seq", time = 0, summary = "m$seq", level = "Info")
    }

    private companion object {
        const val RESTART_TIME = 1_000L
    }
}
