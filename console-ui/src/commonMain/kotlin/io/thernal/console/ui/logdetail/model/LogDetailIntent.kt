package io.thernal.console.ui.logdetail.model

import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.ui.core.ViewIntent

sealed interface LogDetailIntent : ViewIntent {
    data class SelectPage(val pageIndex: Int) : LogDetailIntent
    data class SetQuery(val query: TextFieldValue) : LogDetailIntent
}
