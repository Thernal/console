package io.thernal.console.inspector.ui.view.inspector.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.DsContainer
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun InspectorListSection(
    title: String,
    items: List<String>,
) {
    if (items.isEmpty()) return
    Column {
        DsText(
            text = title,
            style = Theme.typography.label02,
            color = Theme.colors.content04,
            modifier = Modifier.padding(bottom = Theme.dimens.dp4),
        )
        DsContainer(Modifier.fillMaxWidth()) {
            Column {
                items.forEachIndexed { index, item ->
                    if (index > 0) DsDivider()
                    DsText(
                        text = item,
                        style = Theme.typography.body03,
                        modifier = Modifier.padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp6),
                    )
                }
            }
        }
    }
}
