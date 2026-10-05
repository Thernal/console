package io.thernal.console.designsystem.components.core.chip

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

/** A small caption followed by chips that wrap onto as many rows as the width needs. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DsChipGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable FlowRowScope.() -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        if (title != null) {
            DsText(
                text = title,
                style = Theme.typography.label02,
                color = Theme.colors.content04,
                modifier = Modifier.padding(bottom = Theme.dimens.dp2),
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
            content = content,
        )
    }
}
