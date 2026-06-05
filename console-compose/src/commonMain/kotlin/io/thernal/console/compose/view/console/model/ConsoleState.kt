package io.thernal.console.compose.view.console.model

import androidx.compose.runtime.Stable
import io.thernal.console.compose.core.ViewState

@Stable
class ConsoleState : ViewState() {
    val selectedIndex = field(0)
}
