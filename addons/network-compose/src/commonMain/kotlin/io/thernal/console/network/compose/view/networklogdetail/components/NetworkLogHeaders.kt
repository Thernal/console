package io.thernal.console.network.compose.view.networklogdetail.components

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
import io.thernal.console.compose.common.LocalSearchQuery
import io.thernal.console.compose.common.highlight
import io.thernal.console.designsystem.components.core.DsContainer
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.modifier.applyIf
import io.thernal.console.designsystem.components.modifier.focusRing
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.network.compose.common.extensions.containsQuery
import kotlin.collections.component1
import kotlin.collections.component2

@Composable
internal fun NetworkLogHeaders(
    headers: Map<String, String>,
    accentColor: Color,
    expanded: Boolean,
    onToggle: () -> Unit,
) {
    val query = LocalSearchQuery.current.value
    val isFocused = query.isNotBlank() && headers.containsQuery(query)

    DsContainer(
        modifier = Modifier
            .fillMaxWidth()
            .applyIf(isFocused) { focusRing() },
    ) {
        Column {
            SectionHeader(
                title = "Headers",
                badge = headers.size.toString(),
                expanded = expanded,
                accentColor = accentColor,
                onClick = onToggle,
            )

            AnimatedVisibility(visible = expanded) {
                HeadersContent(headers)
            }
        }
    }
}

@Composable
private fun HeadersContent(headers: Map<String, String>) {
    SelectionContainer {
        Column {
            DsDivider()
            headers.entries.forEachIndexed { index, (key, value) ->
                if (index > 0) {
                    DsDivider()
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Theme.dimens.dp12,
                            vertical = Theme.dimens.dp8,
                        ),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = Theme.dimens.dp8,
                    ),
                ) {
                    DsText(
                        text = key.highlight(),
                        color = Theme.colors.content03,
                        modifier = Modifier.weight(0.4f),
                    )

                    DsText(
                        text = value.highlight(),
                        modifier = Modifier.weight(0.6f),
                    )
                }
            }
        }
    }
}
