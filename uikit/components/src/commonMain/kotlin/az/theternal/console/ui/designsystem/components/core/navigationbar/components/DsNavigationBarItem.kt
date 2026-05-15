package az.theternal.console.ui.designsystem.components.core.navigationbar.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import az.theternal.console.ui.designsystem.components.core.LocalDsContentColor
import az.theternal.console.ui.designsystem.components.core.LocalDsTextStyle
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
fun RowScope.DsNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable () -> Unit,
    label: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val contentColor = when {
        !enabled -> Theme.colors.content04
        selected -> Theme.colors.primary01
        else -> Theme.colors.content03
    }

    Column(
        modifier = modifier
            .weight(1f)
            .selectable(
                selected = selected,
                enabled = enabled,
                role = Role.Tab,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(vertical = Theme.dimens.dp4),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp2),
    ) {
        CompositionLocalProvider(LocalDsContentColor provides contentColor) {
            icon()
        }
        CompositionLocalProvider(
            LocalDsContentColor provides contentColor,
            LocalDsTextStyle provides Theme.typography.label01,
        ) {
            label()
        }
    }
}
