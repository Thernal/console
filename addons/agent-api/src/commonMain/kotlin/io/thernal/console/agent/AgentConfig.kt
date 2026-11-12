package io.thernal.console.agent

/**
 * Runtime configuration of the agent bridge.
 *
 * While [enabled] is true the app keeps connecting to the `console-mcp` hub at [hubHost]:[hubPort] and shares
 * what the addons registered in [AgentRegistry], so an AI agent can read it over MCP. [enableActions] also lets the
 * agent run actions, the tools that change the app; it is off by default. [deviceName] replaces the platform's
 * device name in the hub's device list. [historySize] is how many recent feed records the app keeps for a hub that
 * connects later.
 */
data class AgentConfig(
    val enabled: Boolean = true,
    val enableActions: Boolean = false,
    val hubHost: String = DEFAULT_HUB_HOST,
    val hubPort: Int = DEFAULT_HUB_PORT,
    val deviceName: String? = null,
    val historySize: Int = DEFAULT_HISTORY_SIZE,
) {
    internal fun normalized(): AgentConfig {
        return copy(
            hubHost = hubHost.trim().ifEmpty { DEFAULT_HUB_HOST },
            hubPort = hubPort.coerceIn(MIN_PORT, MAX_PORT),
            deviceName = deviceName?.trim()?.ifEmpty { null },
            historySize = historySize.coerceIn(MIN_HISTORY_SIZE, MAX_HISTORY_SIZE),
        )
    }

    companion object {
        const val DEFAULT_HUB_HOST = "127.0.0.1"
        const val DEFAULT_HUB_PORT = 47_474
        const val MIN_PORT = 1
        const val MAX_PORT = 65_535
        const val DEFAULT_HISTORY_SIZE = 2_000
        const val MIN_HISTORY_SIZE = 100
        const val MAX_HISTORY_SIZE = 20_000
    }
}
