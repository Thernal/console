package io.thernal.console.mcp.hub

/** A device as the tools see it: identity, link state, what it shares and how many records the hub holds. */
data class HubDevice(
    val info: HubDeviceInfo,
    val connected: Boolean,
    val address: String,
    val manifest: HubManifest,
    val recordCounts: Map<String, Int>,
) {
    fun hasTool(name: String): Boolean {
        return manifest.tools.any { it.name == name }
    }

    fun hasFeed(name: String): Boolean {
        return manifest.feeds.any { it.name == name } || name in recordCounts
    }
}
