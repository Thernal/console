package io.thernal.console.agent.ui.device

import android.os.Build
import io.thernal.console.io.ConsoleIoContextHolder

internal actual fun readAgentDevice(runId: String): AgentDevice {
    return AgentDevice(
        id = storedDeviceId() ?: runId,
        runId = runId,
        name = "${Build.MANUFACTURER} ${Build.MODEL}",
        platform = "Android",
        osVersion = Build.VERSION.RELEASE,
        appId = ConsoleIoContextHolder.applicationContext?.packageName,
    )
}
