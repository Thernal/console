package io.thernal.console.crash.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun CrashSettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = Theme.dimens.dp16,
                vertical = Theme.dimens.dp12,
            ),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp10),
    ) {
        DsText(
            text = title,
            style = Theme.typography.title02,
            color = Theme.colors.content02,
        )
        content()
    }
}
