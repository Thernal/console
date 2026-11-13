package io.thernal.console.mcp.hub

import kotlinx.serialization.Serializable

/**
 * The device block of the app's `hello` message. [id] stays the same across app restarts where the platform allows;
 * [run] is new per process, so the hub can tell a restart from a reconnect.
 */
@Serializable
data class HubDeviceInfo(
    val id: String,
    val name: String,
    val platform: String,
    val os: String,
    val app: String? = null,
    val run: String? = null,
)
