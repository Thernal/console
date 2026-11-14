package io.thernal.console.mcp.hub

/** The app's answer to a tool call (`type: result`): its text, and the images the tool attached. */
data class HubCallResult(
    val isError: Boolean,
    val text: String,
    val images: List<HubImage> = emptyList(),
)
