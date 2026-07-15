package io.thernal.console.settings

import io.thernal.console.core.ConsoleInternalApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Suppress("UnusedParameter")
object SettingsRegistry {

    val sections: StateFlow<List<SettingsSection>> = MutableStateFlow<List<SettingsSection>>(emptyList()).asStateFlow()

    @ConsoleInternalApi
    fun register(section: SettingsSection) = Unit

    @ConsoleInternalApi
    fun setOnRegistered(listener: (SettingsSection) -> Unit) = Unit
}
