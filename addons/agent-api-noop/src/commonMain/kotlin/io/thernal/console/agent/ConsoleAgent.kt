package io.thernal.console.agent

import io.thernal.console.core.ConsoleInternalApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** No-op stand-in for release builds: same API, nothing is stored, no socket is opened. */
@Suppress("UnusedParameter")
object ConsoleAgent {
    val config: StateFlow<AgentConfig> = MutableStateFlow(AgentConfig()).asStateFlow()
    val connection: StateFlow<AgentConnection> = MutableStateFlow(AgentConnection.Disabled).asStateFlow()

    fun updateConfig(config: AgentConfig) = Unit

    fun updateConfig(transform: AgentConfig.() -> AgentConfig) = Unit

    @ConsoleInternalApi
    fun reportConnection(connection: AgentConnection) = Unit
}
