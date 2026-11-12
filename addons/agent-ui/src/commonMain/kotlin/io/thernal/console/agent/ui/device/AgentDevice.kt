package io.thernal.console.agent.ui.device

/**
 * Identity the app reports to the hub. [id] stays the same across restarts on Android and iOS (see
 * [storedDeviceId]) and is new per process on JVM, where two copies of one app can run side by side. [runId] is new
 * per process, so the hub can tell a restart from a reconnect.
 */
internal data class AgentDevice(
    val id: String,
    val runId: String,
    val name: String,
    val platform: String,
    val osVersion: String,
    val appId: String?,
)
