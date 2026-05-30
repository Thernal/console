package az.theternal.console.compose.view.defaultdetail.model

import androidx.compose.ui.text.input.TextFieldValue
import az.theternal.console.compose.core.Intent

sealed interface DefaultLogDetailIntent : Intent {
    data class SetQuery(val query: TextFieldValue) : DefaultLogDetailIntent
    data object CopyMessage : DefaultLogDetailIntent
}
