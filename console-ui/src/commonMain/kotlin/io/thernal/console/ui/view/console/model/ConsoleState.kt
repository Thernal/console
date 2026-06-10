package io.thernal.console.ui.view.console.model

import androidx.compose.runtime.Stable
import io.thernal.console.ui.core.ViewState

@Stable
class ConsoleState : ViewState() {
    val selectedIndex = field(0)
}
