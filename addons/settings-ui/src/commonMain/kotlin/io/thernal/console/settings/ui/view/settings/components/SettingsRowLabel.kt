package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.ui.common.highlight

/** Shared title + optional description block used by every settings row. */
@Composable
internal fun SettingsRowLabel(
    title: String,
    description: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(end = Theme.dimens.dp16),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp2),
    ) {
        DsText(
            text = title.highlight(),
            style = Theme.typography.body02,
            color = Theme.colors.content01,
        )
        if (description != null) {
            DsText(
                text = description,
                style = Theme.typography.body03,
                color = Theme.colors.content04,
            )
        }
    }
}
