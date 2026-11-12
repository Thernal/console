package io.thernal.console.mcp.hub

/** Whether the hub's TCP port is open for apps. */
sealed interface HubStatus {
    data object Starting : HubStatus

    data class Listening(val address: String) : HubStatus

    data class Failed(val reason: String) : HubStatus
}
