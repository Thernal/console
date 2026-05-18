package az.theternal.console.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import az.theternal.console.designsystem.foundation.theme.LocalDsContentColor
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
fun DsAppBar(
    modifier: Modifier = Modifier,
    containerColor: Color = Theme.colors.background1,
    contentColor: Color = Theme.colors.content01,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    leadingArrangement: Arrangement.Horizontal = Arrangement.Start,
    trailingArrangement: Arrangement.Horizontal = Arrangement.End,
    leading: @Composable RowScope.() -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
    content: @Composable RowScope.() -> Unit = {},
) {
    CompositionLocalProvider(LocalDsContentColor provides contentColor) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(containerColor)
                .windowInsetsPadding(
                    insets = WindowInsets.safeDrawing.only(
                        sides = WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
                    ),
                )
                .defaultMinSize(minHeight = Theme.dimens.dp48)
                .padding(horizontal = Theme.metrics.screenPaddingHorizontal),
            verticalAlignment = verticalAlignment,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = leadingArrangement,
                verticalAlignment = verticalAlignment,
                content = leading,
            )

            content()

            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = trailingArrangement,
                verticalAlignment = verticalAlignment,
                content = trailing,
            )
        }
    }
}
