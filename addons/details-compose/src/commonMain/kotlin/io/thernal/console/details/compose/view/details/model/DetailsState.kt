package io.thernal.console.details.compose.view.details.model

import androidx.compose.runtime.Stable
import io.thernal.console.compose.core.ViewState
import io.thernal.console.details.ConsoleDetails

@Stable
class DetailsState : ViewState() {
    val details = field(ConsoleDetails.flow.value)
}
