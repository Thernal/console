package az.theternal.console.uikit.components.core

import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import az.theternal.console.uikit.foundation.theme.LocalDsContentColor
import az.theternal.console.uikit.foundation.theme.LocalDsTextStyle

@Composable
fun DsText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalDsContentColor.current,
    style: TextStyle = LocalDsTextStyle.current,
    minLines: Int = 1,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    softWrap: Boolean = true,
    autoSize: TextAutoSize? = null,
) {
    BasicText(
        text = text,
        modifier = modifier,
        style = style.copy(color = color),
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        autoSize = autoSize,
    )
}
