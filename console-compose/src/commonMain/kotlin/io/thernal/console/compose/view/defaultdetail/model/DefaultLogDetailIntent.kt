package io.thernal.console.compose.view.defaultdetail.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.compose.core.Intent

sealed interface DefaultLogDetailIntent : Intent {
    data class SetQuery(val query: TextFieldValue) : DefaultLogDetailIntent
    data object CopyMessage : DefaultLogDetailIntent
}
