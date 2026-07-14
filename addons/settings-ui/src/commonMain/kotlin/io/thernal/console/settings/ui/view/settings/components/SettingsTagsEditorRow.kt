package io.thernal.console.settings.ui.view.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsTextField
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.foundation.theme.Theme

private const val DISABLED_ALPHA = 0.4f

/** Chip list + text input; trims and dedupes (a `Set`) on add. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun SettingsTagsEditorRow(
    title: String,
    description: String?,
    tags: Set<String>,
    enabled: Boolean,
    onTagsChange: (Set<String>) -> Unit,
) {
    var input by remember { mutableStateOf(TextFieldValue()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
        verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
    ) {
        SettingsRowLabel(title = title, description = description)

        DsTextField(
            value = input,
            onValueChange = { if (enabled) input = it },
            hint = "Add tag…",
            suffix = {
                if (input.text.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .size(Theme.dimens.dp32)
                            .clip(Theme.rounding.r8)
                            .background(Theme.colors.primary01)
                            .pressable(
                                enabled = enabled,
                                onPress = {
                                    val tag = input.text.trim()
                                    if (tag.isNotEmpty()) onTagsChange(tags + tag)
                                    input = TextFieldValue()
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        DsIcon(
                            icon = Icons.Outlined.Add,
                            size = Theme.metrics.iconSm,
                            color = Theme.colors.primaryContent,
                        )
                    }
                }
            },
        )

        if (tags.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            ) {
                tags.forEach { tag ->
                    DsChip(
                        label = tag,
                        selected = true,
                        modifier = Modifier.pressable(enabled = enabled, onPress = { onTagsChange(tags - tag) }),
                        trailing = {
                            DsIcon(
                                icon = Icons.Outlined.Close,
                                size = Theme.metrics.iconXs,
                                color = Theme.colors.primary01,
                                modifier = Modifier.padding(start = Theme.dimens.dp4),
                            )
                        },
                    )
                }
            }
        }
    }
}
