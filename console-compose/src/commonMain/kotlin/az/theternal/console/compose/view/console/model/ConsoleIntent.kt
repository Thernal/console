package az.theternal.console.compose.view.console.model

import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.compose.core.Intent

sealed interface ConsoleIntent : Intent {
    data class SelectTab(val index: Int) : ConsoleIntent
    data class RequestTab(val tab: ConsoleTab?) : ConsoleIntent
}
