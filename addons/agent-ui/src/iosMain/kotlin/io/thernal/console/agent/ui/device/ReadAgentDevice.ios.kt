package io.thernal.console.agent.ui.device

import platform.Foundation.NSBundle
import platform.Foundation.NSProcessInfo
import platform.UIKit.UIDevice

private const val SIMULATOR_NAME_KEY = "SIMULATOR_DEVICE_NAME"

internal actual fun readAgentDevice(id: String): AgentDevice {
    val device = UIDevice.currentDevice
    val simulatorName = NSProcessInfo.processInfo.environment[SIMULATOR_NAME_KEY] as? String
    return AgentDevice(
        id = id,
        name = simulatorName?.let { "$it (Simulator)" } ?: device.name,
        platform = "iOS",
        osVersion = device.systemVersion,
        appId = NSBundle.mainBundle.bundleIdentifier,
    )
}
