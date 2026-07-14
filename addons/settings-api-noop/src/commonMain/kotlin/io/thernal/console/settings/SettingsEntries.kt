package io.thernal.console.settings

import kotlinx.coroutines.flow.StateFlow

@Suppress("UnusedParameter")
fun <C : Any> settingsEntries(
    id: String,
    title: String,
    order: Int = Int.MAX_VALUE,
    config: StateFlow<C>,
    update: (C.() -> C) -> Unit,
    build: SettingsEntriesScope<C>.() -> Unit,
): SettingsSection = SettingsSection()
