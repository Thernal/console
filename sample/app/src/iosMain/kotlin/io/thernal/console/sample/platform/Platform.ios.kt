package io.thernal.console.sample.platform

import platform.UIKit.UIDevice

actual fun platformLabel(): String {
    val device = UIDevice.currentDevice
    return "${device.systemName} ${device.systemVersion}"
}

actual fun deviceModel(): String {
    val device = UIDevice.currentDevice
    return "${device.model} · ${device.name}"
}
