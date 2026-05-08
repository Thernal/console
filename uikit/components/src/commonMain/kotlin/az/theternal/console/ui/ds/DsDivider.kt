package az.theternal.console.ui.ds

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import az.theternal.console.ui.designsystem.DsTheme

@Composable
fun DsDivider(
    modifier: Modifier = Modifier,
    color: Color = DsTheme.colors.border,
) {
    HorizontalDivider(modifier = modifier, color = color)
}
