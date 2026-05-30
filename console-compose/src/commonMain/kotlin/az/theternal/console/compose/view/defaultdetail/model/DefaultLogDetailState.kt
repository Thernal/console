package az.theternal.console.compose.view.defaultdetail.model

import androidx.compose.ui.text.input.TextFieldValue
import az.theternal.console.compose.core.ViewState

class DefaultLogDetailState : ViewState() {
    val detailQuery = field(TextFieldValue())
    val copyTrigger = field(0)
}
