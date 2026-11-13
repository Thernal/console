package io.thernal.console.agent.ui.device

/** Reads this device's id, name, platform, OS version and app id for the process run [runId]. */
internal expect fun readAgentDevice(runId: String): AgentDevice
