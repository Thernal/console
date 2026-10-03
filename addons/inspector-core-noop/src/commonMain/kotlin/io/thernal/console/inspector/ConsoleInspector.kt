package io.thernal.console.inspector

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** No-op stand-in for release builds: same API, nothing is stored or executed. */
@Suppress("UnusedParameter", "EmptyFunctionBlock")
object ConsoleInspector {
    val config: StateFlow<InspectorConfig> = MutableStateFlow(InspectorConfig()).asStateFlow()
    val commands: SharedFlow<InspectorCommand> = MutableSharedFlow<InspectorCommand>().asSharedFlow()

    fun updateConfig(config: InspectorConfig) {}

    fun updateConfig(transform: InspectorConfig.() -> InspectorConfig) {}

    fun dispatch(command: InspectorCommand) {}
}
