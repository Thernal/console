package io.thernal.console.agent

/** State of the link between the app and the `console-mcp` hub. */
sealed interface AgentConnection {
    /** The bridge is switched off in [AgentConfig.enabled]; no socket is open. */
    data object Disabled : AgentConnection

    /** No hub reachable yet; the bridge keeps retrying with a growing delay. */
    data object Searching : AgentConnection

    /** Connected to the hub at [hubAddress] (`host:port`). */
    data class Connected(val hubAddress: String) : AgentConnection
}
