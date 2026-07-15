package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun SettingsSectionDivider() {
    DsDivider(
        modifier = Modifier.padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp8),
    )
}
