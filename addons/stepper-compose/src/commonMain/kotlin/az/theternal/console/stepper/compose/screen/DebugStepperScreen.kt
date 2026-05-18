package az.theternal.console.stepper.compose.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import az.theternal.console.stepper.DebugStepper
import az.theternal.console.stepper.compose.SteppedEventsRoute
import az.theternal.console.stepper.compose.screen.components.SelectableChip
import az.theternal.console.stepper.compose.screen.components.SettingsSection
import az.theternal.console.stepper.compose.screen.components.SettingsToggleRow
import az.theternal.console.stepper.compose.screen.components.TagChip
import az.theternal.console.stepper.compose.screen.components.TagInputField
import az.theternal.console.runtime.LogLevel
import az.theternal.console.designsystem.components.modifier.pressable
import az.theternal.console.api.ConsoleRoute
import az.theternal.console.api.LocalConsoleNavigator
import az.theternal.console.api.LocalLogRenderer
import az.theternal.console.designsystem.components.core.DsDivider
import az.theternal.console.designsystem.components.core.DsIcon
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.foundation.theme.Theme

private val autoResumeOptions = listOf(null, 3, 5, 10, 30)

private const val CAUGHT_PREVIEW_COUNT = 3

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
                        items(LogLevel.entries.filter { it != LogLevel.None }) { level ->
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
                DsText(
                    text = "Clear",
                    style = Theme.typography.label01,
                    color = Theme.colors.danger,
                    modifier = Modifier.pressable(
                        onPress = { DebugStepper.clearSteppedEvents() },
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pressable(
                            onPress = { navigator.push(SteppedEventsRoute) },
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
                        color = Theme.colors.primary01,
                    )
                }
            }
        }
    }
}
