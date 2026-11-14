package io.thernal.console.mcp.hub

import kotlinx.serialization.Serializable

/** An image in a `result`, as the app sends it: [data] is the base64 of the bytes, [mime] their type. */
@Serializable
data class HubImage(
    val name: String,
    val mime: String,
    val data: String,
)
