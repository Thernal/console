package io.thernal.console.ui.view.console.model

import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.ui.core.ViewIntent

sealed interface ConsoleIntent : ViewIntent {
    data class SelectTab(val index: Int) : ConsoleIntent
    data class RequestTab(val tab: ConsoleTab?) : ConsoleIntent
}
