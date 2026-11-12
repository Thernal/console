package io.thernal.console.agent

import io.thernal.console.core.ConsoleInternalApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Facade of the agent bridge addon: its configuration and the state of the link to the hub. */
object ConsoleAgent {
    private val _config = MutableStateFlow(AgentConfig())
    val config: StateFlow<AgentConfig> = _config.asStateFlow()

    private val _connection = MutableStateFlow<AgentConnection>(AgentConnection.Disabled)
    val connection: StateFlow<AgentConnection> = _connection.asStateFlow()

    fun updateConfig(config: AgentConfig) {
        _config.value = config.normalized()
    }

    fun updateConfig(transform: AgentConfig.() -> AgentConfig) {
        _config.update { it.transform().normalized() }
    }

    @ConsoleInternalApi
    fun reportConnection(connection: AgentConnection) {
        _connection.value = connection
    }
}
