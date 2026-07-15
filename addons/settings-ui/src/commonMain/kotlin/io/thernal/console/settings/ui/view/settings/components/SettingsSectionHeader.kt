package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun SettingsSectionHeader(title: String) {
    DsText(
        text = title,
        style = Theme.typography.title01,
        color = Theme.colors.content02,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
    )
}
