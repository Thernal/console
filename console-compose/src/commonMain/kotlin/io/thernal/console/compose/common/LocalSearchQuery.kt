package io.thernal.console.compose.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import io.thernal.console.designsystem.foundation.extensions.highlight
import io.thernal.console.designsystem.foundation.theme.Theme

val LocalSearchQuery = compositionLocalOf<State<String>> { mutableStateOf("") }

@Composable
fun String.highlight(
    background: Color = Theme.colors.warning,
    content: Color = Theme.colors.warningContent,
): AnnotatedString {
    val query = LocalSearchQuery.current.value

    return remember(this, query) {
        this.highlight(
            query = query,
            background = background,
            color = content,
        )
    }
}
