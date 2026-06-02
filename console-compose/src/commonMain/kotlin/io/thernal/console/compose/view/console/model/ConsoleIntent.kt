package io.thernal.console.compose.view.console.model

import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.compose.core.Intent

sealed interface ConsoleIntent : Intent {
    data class SelectTab(val index: Int) : ConsoleIntent
    data class RequestTab(val tab: ConsoleTab?) : ConsoleIntent
}
