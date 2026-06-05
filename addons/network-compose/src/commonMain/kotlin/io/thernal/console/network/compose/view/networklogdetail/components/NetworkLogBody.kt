package io.thernal.console.network.compose.view.networklogdetail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import io.thernal.console.compose.common.LocalSearchQuery
import io.thernal.console.compose.common.highlight
import io.thernal.console.designsystem.components.core.DsCard
import io.thernal.console.designsystem.components.core.DsContainer
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.modifier.applyIf
import io.thernal.console.designsystem.components.modifier.focusRing
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
internal fun NetworkLogBody(
    title: String,
    body: String,
    accentColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val query = LocalSearchQuery.current.value
    val isFocused = query.isNotBlank() && body.contains(query, ignoreCase = true)

    DsContainer(
        modifier = Modifier
            .fillMaxWidth()
            .applyIf(isFocused) { focusRing() },
    ) {
        Column {
            SectionHeader(
                title = title,
                badge = null,
                expanded = expanded,
                accentColor = accentColor,
                onClick = onToggle,
            )

            AnimatedVisibility(visible = expanded) {
                BodyContent(
                    body = body,
                    accentColor = accentColor,
                )
            }
        }
    }
}

@Composable
private fun BodyContent(
    body: String,
    accentColor: Color,
) {
    DsDivider()

    DsCard(
        modifier = Modifier.padding(Theme.dimens.dp12),
        color = accentColor,
    ) {
        SelectionContainer(modifier = Modifier.weight(1f)) {
            DsText(
                text = body.highlight(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Theme.dimens.dp12,
                        vertical = Theme.dimens.dp12,
                    ),
                style = Theme.typography.body02.copy(
                    fontFamily = FontFamily.Monospace,
                    lineHeight = Theme.typography.body01.lineHeight,
                ),
            )
        }
    }
}
