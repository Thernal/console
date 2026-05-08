package az.theternal.console.ui.ds

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import az.theternal.console.ui.designsystem.DsTheme

@Composable
fun DsText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    style: DsTextStyle? = null,
    maxLines: Int = Int.MAX_VALUE,
    overflow: TextOverflow = TextOverflow.Clip,
) {
    val typo = DsTheme.typography
    val resolved = style?.let {
        when (it) {
            DsTextStyle.BodySmall -> typo.bodySmall
            DsTextStyle.Body -> typo.body
            DsTextStyle.BodyLarge -> typo.bodyLarge
            DsTextStyle.LabelSmall -> typo.labelSmall
            DsTextStyle.LabelMedium -> typo.labelMedium
            DsTextStyle.TitleSmall -> typo.titleSmall
            DsTextStyle.TitleMedium -> typo.titleMedium
        }
    }
    if (resolved != null) {
        Text(
            text = text,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            style = resolved,
            maxLines = maxLines,
            overflow = overflow,
        )
    } else {
        Text(
            text = text,
            modifier = modifier,
            color = color,
            fontSize = fontSize,
            maxLines = maxLines,
            overflow = overflow,
        )
    }
}
