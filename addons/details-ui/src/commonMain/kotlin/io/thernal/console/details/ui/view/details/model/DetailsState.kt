package io.thernal.console.details.ui.view.details.model

import androidx.compose.runtime.Stable
import io.thernal.console.ui.core.ViewState
import io.thernal.console.details.ConsoleDetails

@Stable
class DetailsState : ViewState() {
    val details = field(ConsoleDetails.flow.value)
}
