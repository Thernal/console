package io.thernal.console.logging.ui.view.detail.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.ui.core.ViewIntent

sealed interface LogDetailIntent : ViewIntent {
    data class SelectPage(val pageIndex: Int) : LogDetailIntent
    data class SetQuery(val query: TextFieldValue) : LogDetailIntent
}
