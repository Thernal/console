package az.theternal.console.details.compose.view.details

import androidx.compose.runtime.Stable
import az.theternal.console.compose.core.ViewState
import az.theternal.console.details.ConsoleDetails

@Stable
class DetailsState : ViewState() {
    val details = field(ConsoleDetails.flow.value)
}
