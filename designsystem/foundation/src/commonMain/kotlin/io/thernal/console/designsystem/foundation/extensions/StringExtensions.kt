package io.thernal.console.designsystem.foundation.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString

fun String.highlight(
    query: String,
    background: Color,
    color: Color,
): AnnotatedString {
    val text = this

    if (query.isBlank()) {
        return AnnotatedString(text)
    }

    return buildAnnotatedString {
        val lowerText = text.lowercase()
        val lowerQuery = query.lowercase()
        var cursor = 0
        while (cursor <= this.length) {
            val matchStart = lowerText.indexOf(lowerQuery, cursor)
            if (matchStart == -1) {
                append(text.substring(cursor))
                break
            }
            append(text.substring(cursor, matchStart))
            val matchEnd = matchStart + query.length
            pushStyle(SpanStyle(background = background, color = color))
            append(text.substring(matchStart, matchEnd))
            pop()
            cursor = matchEnd
        }
    }
}
