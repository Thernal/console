package az.theternal.console.ui.designsystem.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun DsTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Theme.colors.background1,
    contentColor: Color = Theme.colors.content01,
    titleTextStyle: TextStyle = Theme.typography.title01,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
) {
    CompositionLocalProvider(LocalDsContentColor provides contentColor) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    insets = WindowInsets.safeDrawing.only(
                        sides = WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
                    ),
                )
                .height(Theme.dimens.dp48)
                .background(containerColor)
                .padding(horizontal = Theme.dimens.dp4),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.width(Theme.metrics.minTouchTarget),
                contentAlignment = Alignment.Center,
            ) {
                navigationIcon?.invoke()
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = Theme.dimens.dp4),
                contentAlignment = Alignment.CenterStart,
            ) {
                CompositionLocalProvider(LocalDsTextStyle provides titleTextStyle) {
                    title()
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp2),
                verticalAlignment = Alignment.CenterVertically,
                content = actions,
            )
        }
    }
}
