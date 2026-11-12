package io.thernal.console.agent.ui.device

/** Identity the app reports to the hub; [id] is new per process, so a restarted app shows up as a new session. */
internal data class AgentDevice(
    val id: String,
    val name: String,
    val platform: String,
    val osVersion: String,
    val appId: String?,
)
