package io.thernal.console.mcp.tools

/** A tool's text answer. */
data class ToolText(
    val text: String,
    val isError: Boolean = false,
)
