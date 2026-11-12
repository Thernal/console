package io.thernal.console.mcp.hub

import kotlinx.serialization.Serializable

/** The device block of the app's `hello` message. */
@Serializable
data class HubDeviceInfo(
    val id: String,
    val name: String,
    val platform: String,
    val os: String,
    val app: String? = null,
)
