package io.thernal.console.agent

import io.thernal.console.core.ConsoleInternalApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Suppress("UnusedParameter")
object AgentRegistry {
    val entries: StateFlow<List<AgentEntries>> = MutableStateFlow<List<AgentEntries>>(emptyList()).asStateFlow()

    @ConsoleInternalApi
    fun register(entries: AgentEntries) = Unit

    @ConsoleInternalApi
    fun redactedKeys(): Set<String> {
        return emptySet()
    }
}
