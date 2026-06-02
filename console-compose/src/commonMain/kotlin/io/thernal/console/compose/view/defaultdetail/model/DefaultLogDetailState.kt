package io.thernal.console.compose.view.defaultdetail.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.compose.core.ViewState

class DefaultLogDetailState : ViewState() {
    val detailQuery = field(TextFieldValue())
    val copyTrigger = field(0)
}
