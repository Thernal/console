package io.thernal.console.inspector

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/** No-op stand-in for release builds: same API, nothing is stored or executed. */
@Suppress("UnusedParameter")
object ConsoleInspector {
    val config: StateFlow<InspectorConfig> = MutableStateFlow(InspectorConfig()).asStateFlow()
    val commands: SharedFlow<InspectorCommand> = MutableSharedFlow<InspectorCommand>().asSharedFlow()

    fun updateConfig(config: InspectorConfig) = Unit

    fun updateConfig(transform: InspectorConfig.() -> InspectorConfig) = Unit

    fun dispatch(command: InspectorCommand) = Unit
}
