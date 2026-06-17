package io.thernal.console.network.ui.view.networklogdetail.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import io.thernal.console.ui.common.LocalSearchQuery
import io.thernal.console.ui.common.highlight
import io.thernal.console.designsystem.components.core.DsCard
import io.thernal.console.designsystem.components.core.DsContainer
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.modifier.applyIf
import io.thernal.console.designsystem.components.modifier.focusRing
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.network.NetworkBody
import io.thernal.console.network.toHumanReadableSize

@Composable
internal fun NetworkLogBody(
    title: String,
    body: NetworkBody,
    accentColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val query = LocalSearchQuery.current.value
    val isFocused = query.isNotBlank() && body is NetworkBody.Text &&
        body.value.contains(query, ignoreCase = true)

    DsContainer(
        modifier = Modifier
            .fillMaxWidth()
            .applyIf(isFocused) { focusRing() },
    ) {
        Column {
            SectionHeader(
                title = title,
                badge = body.badge(),
                expanded = expanded,
                accentColor = accentColor,
                onClick = onToggle,
            )

            AnimatedVisibility(visible = expanded) {
                when (body) {
                    is NetworkBody.Text -> TextBodyContent(
                        text = body.value,
                        accentColor = accentColor,
                    )

                    is NetworkBody.Binary -> BinaryBodyContent(
                        body = body,
                        accentColor = accentColor,
                    )
                }
            }
        }
    }
}

private fun NetworkBody.badge(): String? {
    return when (this) {
        is NetworkBody.Text -> null
        is NetworkBody.Binary -> "Binary"
    }
}

@Composable
private fun TextBodyContent(
    text: String,
    accentColor: Color,
) {
    DsDivider()

    DsCard(
        modifier = Modifier.padding(Theme.dimens.dp12),
        color = accentColor,
    ) {
        SelectionContainer(modifier = Modifier.weight(1f)) {
            DsText(
                text = text.highlight(),
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

@Composable
private fun BinaryBodyContent(
    body: NetworkBody.Binary,
    accentColor: Color,
) {
    DsDivider()

    DsCard(
        modifier = Modifier.padding(Theme.dimens.dp12),
        color = accentColor,
    ) {
        SelectionContainer {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Theme.dimens.dp12),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            ) {
                DsText(
                    text = "Binary content is not rendered.",
                    color = Theme.colors.content03,
                )

                MetadataRow(label = "Content Type", value = body.mimeType ?: "Unknown")

                body.byteCount?.let { bytes ->
                    MetadataRow(label = "Content Length", value = "$bytes bytes")
                    MetadataRow(label = "Size", value = bytes.toHumanReadableSize())
                }
            }
        }
    }
}

@Composable
private fun MetadataRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
    ) {
        DsText(
            text = label,
            color = Theme.colors.content03,
            modifier = Modifier.weight(0.4f),
        )

        DsText(
            text = value,
            modifier = Modifier.weight(0.6f),
        )
    }
}
