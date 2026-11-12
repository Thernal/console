package io.thernal.console.agent.ui.device

import android.os.Build
import io.thernal.console.io.ConsoleIoContextHolder

internal actual fun readAgentDevice(id: String): AgentDevice {
    return AgentDevice(
        id = id,
        name = "${Build.MANUFACTURER} ${Build.MODEL}",
        platform = "Android",
        osVersion = Build.VERSION.RELEASE,
        appId = ConsoleIoContextHolder.applicationContext?.packageName,
    )
}
