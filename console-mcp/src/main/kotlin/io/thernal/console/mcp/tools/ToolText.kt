package io.thernal.console.mcp.tools

import io.thernal.console.mcp.hub.HubImage

/** A tool's answer: its text, and the images an app attached, which the agent gets as image content. */
data class ToolText(
    val text: String,
    val isError: Boolean = false,
    val images: List<HubImage> = emptyList(),
)
