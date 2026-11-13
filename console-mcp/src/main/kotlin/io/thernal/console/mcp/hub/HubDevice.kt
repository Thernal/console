package io.thernal.console.mcp.hub

/**
 * A device as the tools see it: identity, link state, what it shares, how many records the hub holds, and how many
 * times the app ran while the hub was up ([runs] above 1 means it restarted, after a crash for example).
 */
data class HubDevice(
    val info: HubDeviceInfo,
    val connected: Boolean,
    val address: String,
    val manifest: HubManifest,
    val recordCounts: Map<String, Int>,
    val runs: Int = 1,
) {
    fun hasTool(name: String): Boolean {
        return manifest.tools.any { it.name == name }
    }

    fun hasFeed(name: String): Boolean {
        return manifest.feeds.any { it.name == name } || name in recordCounts
    }
}
