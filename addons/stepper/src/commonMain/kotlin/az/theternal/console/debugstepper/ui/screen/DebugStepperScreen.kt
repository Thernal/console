package az.theternal.console.debugstepper.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import az.theternal.console.ui.designsystem.components.provider.ThemeProvider
import az.theternal.console.ui.designsystem.foundation.theme.DsPreview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import az.theternal.console.runtime.model.LogLevel
import az.theternal.console.debugstepper.DebugStepper
import az.theternal.console.debugstepper.ui.SteppedEventsRoute
import az.theternal.console.ui.nav.ConsoleRoute
import az.theternal.console.ui.nav.LocalConsoleNavigator
import az.theternal.console.ui.renderer.LocalLogRenderer
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsDivider
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsSwitch
import az.theternal.console.ui.designsystem.components.core.DsText

private val autoResumeOptions = listOf(null, 3, 5, 10, 30)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DebugStepperScreen() {
    val config by DebugStepper.config.collectAsState()
    val state by DebugStepper.state.collectAsState()
    val renderer = LocalLogRenderer.current
    val navigator = LocalConsoleNavigator.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Theme.dimens.dp16),
    ) {
        // Active toggle
        item {
            SettingsToggleRow(
                label = "Active",
                description = "Enable or disable the debug stepper",
                checked = config.enabled,
                onCheckedChange = { DebugStepper.updateConfig { copy(enabled = it) } },
            )
        }

        if (!config.enabled) return@LazyColumn

        item { DsDivider() }

        // Pause toggle
        item {
            SettingsToggleRow(
                label = "Pause",
                description = "Hold logs until you step through them",
                checked = config.paused,
                onCheckedChange = { DebugStepper.updateConfig { copy(paused = it) } },
            )
        }

        if (!config.paused) return@LazyColumn

        item { DsDivider() }

        // Pause-on-match toggle
        item {
            SettingsToggleRow(
                label = "Pause on match",
                description = "Pause only when a log matches the filter",
                checked = config.pauseOnMatch,
                onCheckedChange = { DebugStepper.updateConfig { copy(pauseOnMatch = it) } },
            )
        }

        if (config.pauseOnMatch) {
            item { DsDivider() }

            // Tag filter
            item {
                var tagInput by remember { mutableStateOf("") }
                SettingsSection(title = "Tags") {
                    if (config.pauseOnTags.isEmpty()) {
                        DsText(
                            text = "Matching all tags",
                            style = Theme.typography.body03,
                            color = Theme.colors.content04,
                        )
                    } else {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
                            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
                        ) {
                            config.pauseOnTags.forEach { tag ->
                                TagChip(
                                    tag = tag,
                                    onRemove = {
                                        DebugStepper.updateConfig { copy(pauseOnTags = pauseOnTags - tag) }
                                    },
                                )
                            }
                        }
                    }

                    TagInputField(
                        value = tagInput,
                        onValueChange = { tagInput = it.uppercase() },
                        onAdd = {
                            DebugStepper.updateConfig { copy(pauseOnTags = pauseOnTags + tagInput.trim()) }
                            tagInput = ""
                        },
                    )
                }
            }

            item { DsDivider() }

            // Level filter
            item {
                SettingsSection(title = "Level") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
                    ) {
                        item {
                            SelectableChip(
                                label = "All",
                                selected = config.pauseOnLevelAtLeast == null,
                                onClick = { DebugStepper.updateConfig { copy(pauseOnLevelAtLeast = null) } },
                            )
                        }
                        items(enumValues<LogLevel>()) { level ->
                            SelectableChip(
                                label = level.name,
                                selected = config.pauseOnLevelAtLeast == level,
                                onClick = { DebugStepper.updateConfig { copy(pauseOnLevelAtLeast = level) } },
                            )
                        }
                    }
                }
            }
        }

        item { DsDivider() }

        // Auto-resume
        item {
            SettingsSection(title = "Auto-resume") {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
                ) {
                    items(autoResumeOptions) { seconds ->
                        SelectableChip(
                            label = if (seconds == null) "Off" else "${seconds}s",
                            selected = config.autoResumeSeconds == seconds,
                            onClick = { DebugStepper.updateConfig { copy(autoResumeSeconds = seconds) } },
                        )
                    }
                }
            }
        }

        if (state.steppedEvents.isEmpty()) return@LazyColumn

        item { DsDivider() }

        // Caught events header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Theme.dimens.dp16,
                        vertical = Theme.dimens.dp10,
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp6),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DsText(
                        text = "Caught",
                        style = Theme.typography.title02,
                        color = Theme.colors.content01,
                    )
                    Box(
                        modifier = Modifier
                            .clip(Theme.rounding.r4)
                            .background(Theme.colors.background3)
                            .padding(horizontal = Theme.dimens.dp6, vertical = Theme.dimens.dp2),
                    ) {
                        DsText(
                            text = "${state.steppedEvents.size}",
                            style = Theme.typography.label02,
                            color = Theme.colors.content02,
                        )
                    }
                }
                val interactionSource = remember { MutableInteractionSource() }
                DsText(
                    text = "Clear",
                    style = Theme.typography.label01,
                    color = Theme.colors.danger,
                    modifier = Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = { DebugStepper.clearSteppedEvents() },
                    ),
                )
            }
        }

        val preview = state.steppedEvents.takeLast(CAUGHT_PREVIEW_COUNT).asReversed()

        items(
            items = preview,
            key = { it.id },
            contentType = { "log_item" },
        ) { log ->
            Box(modifier = Modifier.padding(vertical = Theme.dimens.dp3)) {
                renderer.Item(
                    log = log,
                    onClick = { navigator.push(ConsoleRoute.LogDetail("", log.id)) },
                )
            }
        }

        if (state.steppedEvents.size > CAUGHT_PREVIEW_COUNT) {
            item {
                val interactionSource = remember { MutableInteractionSource() }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { navigator.push(SteppedEventsRoute) },
                        )
                        .padding(
                            horizontal = Theme.dimens.dp16,
                            vertical = Theme.dimens.dp12,
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DsText(
                        text = "See all ${state.steppedEvents.size} events",
                        style = Theme.typography.label01,
                        color = Theme.colors.primary01,
                    )
                    DsIcon(
                        icon = Icons.AutoMirrored.Outlined.ArrowForward,
                        size = Theme.metrics.iconSm,
                        tint = Theme.colors.primary01,
                    )
                }
            }
        }
    }
}

private const val CAUGHT_PREVIEW_COUNT = 3

@Composable
private fun SettingsSection(
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

@Composable
private fun SettingsToggleRow(
    label: String,
    description: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = Theme.dimens.dp16),
            verticalArrangement = Arrangement.spacedBy(Theme.dimens.dp2),
        ) {
            DsText(
                text = label,
                style = Theme.typography.body01,
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
        DsSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
private fun TagChip(
    tag: String,
    onRemove: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            .clip(Theme.rounding.r6)
            .background(Theme.colors.primary01.copy(alpha = 0.12f))
            .border(
                width = Theme.metrics.borderWidth,
                color = Theme.colors.primary01.copy(alpha = 0.25f),
                shape = Theme.rounding.r6,
            )
            .padding(
                start = Theme.dimens.dp8,
                end = Theme.dimens.dp4,
                top = Theme.dimens.dp6,
                bottom = Theme.dimens.dp6,
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp4),
    ) {
        DsText(
            text = tag,
            style = Theme.typography.label01,
            color = Theme.colors.primary01,
        )
        Box(
            modifier = Modifier
                .size(Theme.dimens.dp16)
                .clip(Theme.rounding.r4)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onRemove,
                ),
            contentAlignment = Alignment.Center,
        ) {
            DsIcon(
                icon = Icons.Outlined.Close,
                size = Theme.metrics.iconXs,
                tint = Theme.colors.primary01,
            )
        }
    }
}

@Composable
private fun TagInputField(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Theme.rounding.r8)
            .background(Theme.colors.background3)
            .border(
                width = Theme.metrics.borderWidth,
                color = Theme.colors.border,
                shape = Theme.rounding.r8,
            )
            .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp8),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = Theme.typography.body02.copy(color = Theme.colors.content01),
            cursorBrush = SolidColor(Theme.colors.primary01),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    DsText(
                        text = "Add tag…",
                        style = Theme.typography.body02,
                        color = Theme.colors.content04,
                    )
                }
                inner()
            },
        )
        if (value.isNotBlank()) {
            val interactionSource = remember { MutableInteractionSource() }
            Box(
                modifier = Modifier
                    .size(Theme.dimens.dp24)
                    .clip(Theme.rounding.r6)
                    .background(Theme.colors.primary01)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onAdd,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                DsIcon(
                    icon = Icons.Outlined.Add,
                    size = Theme.metrics.iconSm,
                    tint = Theme.colors.primaryContent,
                )
            }
        }
    }
}

@Composable
private fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val bg = if (selected) Theme.colors.primary01 else Theme.colors.background3
    val fg = if (selected) Theme.colors.primaryContent else Theme.colors.content03

    Box(
        modifier = Modifier
            .clip(Theme.rounding.r6)
            .background(bg)
            .then(
                if (!selected) {
                    Modifier.border(Theme.metrics.borderWidth, Theme.colors.border, Theme.rounding.r6)
                } else {
                    Modifier
                },
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = Theme.dimens.dp10, vertical = Theme.dimens.dp8),
    ) {
        DsText(
            text = label,
            style = Theme.typography.label01,
            color = fg,
        )
    }
}

@DsPreview
@Composable
private fun PreviewSettingsToggleRow() {
    ThemeProvider {
        SettingsToggleRow(
            label = "Active",
            description = "Enable or disable the debug stepper",
            checked = true,
            onCheckedChange = {},
        )
    }
}

@DsPreview
@Composable
private fun PreviewSelectableChips() {
    ThemeProvider {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp8),
            modifier = Modifier.padding(Theme.dimens.dp16),
        ) {
            SelectableChip(label = "All", selected = true, onClick = {})
            SelectableChip(label = "Info", selected = false, onClick = {})
            SelectableChip(label = "Error", selected = false, onClick = {})
        }
    }
}

@DsPreview
@Composable
private fun PreviewTagChip() {
    ThemeProvider {
        Row(modifier = Modifier.padding(Theme.dimens.dp16)) {
            TagChip(tag = "NETWORK", onRemove = {})
        }
    }
}
