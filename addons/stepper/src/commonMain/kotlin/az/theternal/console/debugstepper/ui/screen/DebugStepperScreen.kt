package az.theternal.console.debugstepper.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import az.theternal.console.runtime.model.LogLevel
import az.theternal.console.debugstepper.DebugStepper
import az.theternal.console.ui.nav.ConsoleRoute
import az.theternal.console.ui.nav.LocalConsoleNavigator
import az.theternal.console.ui.renderer.LocalLogRenderer
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsDivider
import az.theternal.console.ui.designsystem.components.core.DsIcon
import az.theternal.console.ui.designsystem.components.core.DsSwitch
import az.theternal.console.ui.designsystem.components.core.DsText

private val autoResumeOptions = listOf(null, 3, 5, 10, 30)
private val ScreenHorizontalPadding = Theme.dimens.dp12
private val ScreenVerticalInset = Theme.dimens.dp6
private val SectionVerticalPadding = Theme.dimens.dp10
private val SectionSpacing = Theme.dimens.dp10
private val InlineSpacing = Theme.dimens.dp6
private val CompactInlineSpacing = Theme.dimens.dp4
private val ChipHorizontalPadding = Theme.dimens.dp10
private val ChipVerticalPadding = Theme.dimens.dp4

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DebugStepperScreen() {
    val config by DebugStepper.config.collectAsState()
    val state by DebugStepper.state.collectAsState()
    val renderer = LocalLogRenderer.current
    val navigator = LocalConsoleNavigator.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = ScreenVerticalInset),
    ) {
        item {
            ToggleRow(
                label = "Active",
                checked = config.enabled,
                onCheckedChange = { DebugStepper.updateConfig { copy(enabled = it) } },
                modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = Theme.dimens.dp8),
            )
        }

        if (!config.enabled) return@LazyColumn

        item { DsDivider() }

        item {
            ToggleRow(
                label = "Pause",
                checked = config.paused,
                onCheckedChange = { DebugStepper.updateConfig { copy(paused = it) } },
                modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = Theme.dimens.dp8),
            )
        }

        if (!config.paused) return@LazyColumn

        item { DsDivider() }

        item {
            ToggleRow(
                label = "Pause on match",
                checked = config.pauseOnMatch,
                onCheckedChange = { DebugStepper.updateConfig { copy(pauseOnMatch = it) } },
                modifier = Modifier.padding(horizontal = ScreenHorizontalPadding, vertical = Theme.dimens.dp8),
            )
        }

        if (config.pauseOnMatch) {
            item { DsDivider() }

            item {
                var tagInput by remember { mutableStateOf("") }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ScreenHorizontalPadding, vertical = SectionVerticalPadding),
                    verticalArrangement = Arrangement.spacedBy(SectionSpacing),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DsText("Tags", style = Theme.typography.label01)
                        if (config.pauseOnTags.isEmpty()) {
                            DsText("all", style = Theme.typography.label02, color = Theme.colors.content04)
                        }
                    }

                    if (config.pauseOnTags.isNotEmpty()) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(CompactInlineSpacing),
                            verticalArrangement = Arrangement.spacedBy(CompactInlineSpacing),
                        ) {
                            config.pauseOnTags.forEach { tag ->
                                Row(
                                    modifier = Modifier
                                        .clip(Theme.rounding.r4)
                                        .background(Theme.colors.primary01.copy(alpha = 0.15f))
                                        .padding(
                                            start = Theme.dimens.dp8,
                                            end = Theme.dimens.dp4,
                                            top = Theme.dimens.dp4,
                                            bottom = Theme.dimens.dp4,
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(CompactInlineSpacing),
                                ) {
                                    DsText(tag, style = Theme.typography.label02, color = Theme.colors.primary01)
                                    DsIcon(
                                        icon = Icons.Outlined.Close,
                                        size = Theme.metrics.iconXs,
                                        tint = Theme.colors.primary01,
                                        modifier = Modifier
                                            .padding(Theme.dimens.dp2)
                                            .clickable {
                                                DebugStepper.updateConfig { copy(pauseOnTags = pauseOnTags - tag) }
                                            },
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(Theme.rounding.r6)
                            .background(Theme.colors.background3)
                            .padding(horizontal = Theme.dimens.dp10, vertical = Theme.dimens.dp6),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(InlineSpacing),
                    ) {
                        BasicTextField(
                            value = tagInput,
                            onValueChange = { tagInput = it.uppercase() },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            textStyle = Theme.typography.body02.copy(color = Theme.colors.content01),
                            cursorBrush = SolidColor(Theme.colors.primary01),
                            decorationBox = { inner ->
                                if (tagInput.isEmpty()) {
                                    DsText(
                                        "Tag name...",
                                        style = Theme.typography.body02,
                                        color = Theme.colors.content04,
                                    )
                                }
                                inner()
                            },
                        )
                        if (tagInput.isNotBlank()) {
                            DsIcon(
                                icon = Icons.Outlined.Add,
                                size = Theme.metrics.iconSm,
                                tint = Theme.colors.primary01,
                                modifier = Modifier
                                    .clickable {
                                        DebugStepper.updateConfig { copy(pauseOnTags = pauseOnTags + tagInput.trim()) }
                                        tagInput = ""
                                    },
                            )
                        }
                    }
                }
            }

            item { DsDivider() }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = ScreenHorizontalPadding, vertical = SectionVerticalPadding),
                    verticalArrangement = Arrangement.spacedBy(SectionSpacing),
                ) {
                    DsText("Level", style = Theme.typography.label01)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(CompactInlineSpacing)) {
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

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenHorizontalPadding, vertical = SectionVerticalPadding),
                verticalArrangement = Arrangement.spacedBy(SectionSpacing),
            ) {
                DsText("Auto-resume", style = Theme.typography.label01)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(CompactInlineSpacing)) {
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

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ScreenHorizontalPadding, vertical = SectionVerticalPadding),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(CompactInlineSpacing),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DsText("Caught", style = Theme.typography.label01)
                    DsText(
                        text = "${state.steppedEvents.size}",
                        style = Theme.typography.label02,
                        color = Theme.colors.content03,
                    )
                }
                DsText(
                    text = "Clear",
                    style = Theme.typography.label02,
                    color = Theme.colors.danger,
                    modifier = Modifier.clickable { DebugStepper.clearSteppedEvents() },
                )
            }
        }

        items(state.steppedEvents.asReversed(), key = { it.id }) { log ->
            renderer.Item(
                log = log,
                onClick = { navigator.push(ConsoleRoute.LogDetail("", log.id)) },
            )
        }
    }
}

@Composable
private fun SelectableChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) Theme.colors.primary01 else Theme.colors.background3
    val fg = if (selected) Theme.colors.primaryContent else Theme.colors.content03

    Box(
        modifier = Modifier
            .clip(Theme.rounding.r6)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = ChipHorizontalPadding, vertical = ChipVerticalPadding),
    ) {
        DsText(label, style = Theme.typography.label02, color = fg)
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(label, style = Theme.typography.body02)
        DsSwitch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
