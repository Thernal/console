package io.thernal.console.mcp.hub

/** The app's answer to a tool call (`type: result`). */
data class HubCallResult(
    val isError: Boolean,
    val text: String,
)
