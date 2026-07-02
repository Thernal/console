package io.thernal.console.crash.ui.view.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.core.DsTextField
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

/** Tag editor mirroring the Stepper settings: a text field to add tags, chips to remove them. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CrashSettingsTagsSection(
    title: String,
    emptyHint: String,
    tags: State<Set<String>>,
    tagInput: State<TextFieldValue>,
    onTagInputChange: (TextFieldValue) -> Unit,
    onAddTag: () -> Unit,
    onRemoveTag: (String) -> Unit,
) {
    CrashSettingsSection(title = title) {
        TagInputField(
            value = tagInput.value,
            onValueChange = onTagInputChange,
            onAdd = onAddTag,
        )
        if (tags.value.isEmpty()) {
            DsText(
                text = emptyHint,
                style = Theme.typography.body03,
                color = Theme.colors.content04,
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
                verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            ) {
                tags.value.forEach { tag ->
                    DsChip(
                        label = tag,
                        color = Theme.colors.primary01,
                        selected = true,
                        modifier = Modifier.pressable(
                            onPress = { onRemoveTag(tag) },
                        ),
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

@Composable
private fun TagInputField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onAdd: () -> Unit,
) {
    DsTextField(
        value = value,
        onValueChange = onValueChange,
        hint = "Add tag…",
        suffix = {
            if (value.text.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .size(Theme.dimens.dp32)
                        .clip(Theme.rounding.r8)
                        .background(Theme.colors.primary01)
                        .pressable(onPress = onAdd),
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
}

@DsPreview
@Composable
private fun PreviewCrashSettingsTagsSection() {
    ThemeProvider {
        CrashSettingsTagsSection(
            title = "Include tags",
            emptyHint = "Matching all tags",
            tags = remember { mutableStateOf(setOf("Network", "Auth")) },
            tagInput = remember { mutableStateOf(TextFieldValue()) },
            onTagInputChange = {},
            onAddTag = {},
            onRemoveTag = {},
        )
    }
}
