package io.thernal.console.agent.ui.device

/** Reads this device's name, platform, OS version and app id. */
internal expect fun readAgentDevice(id: String): AgentDevice
