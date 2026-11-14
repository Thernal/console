package io.thernal.console.agent

/**
 * An image a tool answers with beside its text, such as a screenshot; add it with [AgentCall.attach]. The agent sees
 * it as MCP image content, and the hub saves it as a file on the computer and names the path in the answer, so the
 * agent can show it to the person. [name] starts the file name (`ui_snapshot` → `ui_snapshot-<time>.png`).
 */
class AgentImage(
    val bytes: ByteArray,
    val name: String,
    val mimeType: String = PNG,
) {
    companion object {
        const val PNG = "image/png"
    }
}
