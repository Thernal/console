package io.thernal.console.agent

/** No-op stand-in for the real [AgentImage]: the same shape, never sent anywhere. */
class AgentImage(
    val bytes: ByteArray,
    val name: String,
    val mimeType: String = PNG,
) {
    companion object {
        const val PNG = "image/png"
    }
}
