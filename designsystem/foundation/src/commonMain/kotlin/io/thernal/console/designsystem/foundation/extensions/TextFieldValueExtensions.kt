package io.thernal.console.designsystem.foundation.extensions

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

fun TextFieldValue.Companion.from(value: String): TextFieldValue {
    return TextFieldValue(
        text = value,
        selection = TextRange(value.length),
    )
}
