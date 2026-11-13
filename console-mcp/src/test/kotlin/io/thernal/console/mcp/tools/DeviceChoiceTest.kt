package io.thernal.console.mcp.tools

import io.thernal.console.mcp.hub.HubDevice
import io.thernal.console.mcp.hub.HubDeviceInfo
import io.thernal.console.mcp.hub.HubManifest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DeviceChoiceTest {
    @Test
    fun `a name that matches a gone app and a connected one picks the connected one`() {
        val gone = device(id = "old", connected = false)
        val here = device(id = "new", connected = true)

        val choice = DeviceChoice.resolve(listOf(gone, here), argument = "honor") { true }

        assertEquals("new", assertIs<DeviceChoice.Found>(choice).device.info.id)
    }

    @Test
    fun `two connected apps with the same name stay ambiguous`() {
        val devices = listOf(device(id = "a", connected = true), device(id = "b", connected = true))

        val choice = DeviceChoice.resolve(devices, argument = "honor") { true }

        assertIs<DeviceChoice.Problem>(choice)
    }

    private fun device(
        id: String,
        connected: Boolean,
    ): HubDevice {
        return HubDevice(
            info = HubDeviceInfo(id = id, name = "HONOR ABR-LX1", platform = "Android", os = "15"),
            connected = connected,
            address = "usb",
            manifest = HubManifest(),
            recordCounts = emptyMap(),
        )
    }
}
