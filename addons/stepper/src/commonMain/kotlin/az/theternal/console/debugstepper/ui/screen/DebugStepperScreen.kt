package az.theternal.console.debugstepper.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import az.theternal.console.core.base.LogLevel
import az.theternal.console.debugstepper.DebugStepper
import az.theternal.console.ui.ConsoleRoute
import az.theternal.console.ui.LocalConsoleNavigator
import az.theternal.console.ui.LocalLogRenderer
import az.theternal.console.ui.designsystem.DsTheme
import az.theternal.console.ui.ds.DsDivider
import az.theternal.console.ui.ds.DsIcon
import az.theternal.console.ui.ds.DsText
import az.theternal.console.ui.ds.DsTextStyle

private val autoResumeOptions = listOf(null, 3, 5, 10, 30)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun DebugStepperScreen() {
    val state by DebugStepper.state.collectAsState()
    val renderer = LocalLogRenderer.current
    val navigator = LocalConsoleNavigator.current

    LazyColumn(Modifier.fillMaxSize()) {
        // ── Enable toggle ─────────────────────────────────────────────────────
        item {
            ToggleRow(
                label = "Active",
                checked = state.enabled,
                onCheckedChange = { DebugStepper.setEnabled(it) },
                modifier = Modifier.padding(horizontal = DsTheme.dimens.md, vertical = DsTheme.dimens.xs),
            )
        }

        if (!state.enabled) return@LazyColumn

        item { DsDivider() }

        // ── Pause toggle ──────────────────────────────────────────────────────
        item {
            ToggleRow(
                label = "Pause",
                checked = state.paused,
                onCheckedChange = { DebugStepper.setPaused(it) },
                modifier = Modifier.padding(horizontal = DsTheme.dimens.md, vertical = DsTheme.dimens.xs),
            )
        }

        if (!state.paused) return@LazyColumn

        item { DsDivider() }

        // ── Pause on match toggle ─────────────────────────────────────────────
        item {
            ToggleRow(
                label = "Pause on match",
                checked = state.pauseOnMatch,
                onCheckedChange = { DebugStepper.setPauseOnMatch(it) },
                modifier = Modifier.padding(horizontal = DsTheme.dimens.md, vertical = DsTheme.dimens.xs),
            )
        }

        if (state.pauseOnMatch) {
            item { DsDivider() }

            // ── Tag filter ────────────────────────────────────────────────────
            item {
                var tagInput by remember { mutableStateOf("") }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DsTheme.dimens.md, vertical = DsTheme.dimens.sm),
                    verticalArrangement = Arrangement.spacedBy(DsTheme.dimens.sm),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DsText("Tags", style = DsTextStyle.LabelMedium)
                        if (state.pauseOnTags.isEmpty()) {
                            DsText("all", style = DsTextStyle.LabelSmall, color = DsTheme.colors.content4)
                        }
                    }

                    if (state.pauseOnTags.isNotEmpty()) {
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.xs)) {
                            state.pauseOnTags.forEach { tag ->
                                Row(
                                    modifier = Modifier
                                        .padding(bottom = DsTheme.dimens.xs)
                                        .clip(DsTheme.rounding.xs)
                                        .background(DsTheme.colors.primary.copy(alpha = 0.15f))
                                        .padding(
                                            start = DsTheme.dimens.xs,
                                            end = DsTheme.dimens.xxs,
                                            top = DsTheme.dimens.xxs,
                                            bottom = DsTheme.dimens.xxs,
                                        ),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.xxs),
                                ) {
                                    DsText(tag, style = DsTextStyle.LabelSmall, color = DsTheme.colors.primary)
                                    DsIcon(
                                        imageVector = Icons.Outlined.Close,
                                        contentDescription = "Remove",
                                        tint = DsTheme.colors.primary,
                                        modifier = Modifier
                                            .size(DsTheme.metrics.iconXs)
                                            .clickable { DebugStepper.removePauseTag(tag) },
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(DsTheme.rounding.sm)
                            .background(DsTheme.colors.background3)
                            .padding(horizontal = DsTheme.dimens.sm, vertical = DsTheme.dimens.xs),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.xs),
                    ) {
                        BasicTextField(
                            value = tagInput,
                            onValueChange = { tagInput = it.uppercase() },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            textStyle = TextStyle(color = DsTheme.colors.content1, fontSize = 13.sp),
                            cursorBrush = SolidColor(DsTheme.colors.primary),
                            decorationBox = { inner ->
                                if (tagInput.isEmpty()) {
                                    DsText("Tag name...", style = DsTextStyle.Body, color = DsTheme.colors.content4)
                                }
                                inner()
                            },
                        )
                        if (tagInput.isNotBlank()) {
                            DsIcon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = "Add",
                                tint = DsTheme.colors.primary,
                                modifier = Modifier
                                    .size(DsTheme.metrics.iconSm)
                                    .clickable {
                                        DebugStepper.addPauseTag(tagInput.trim())
                                        tagInput = ""
                                    },
                            )
                        }
                    }
                }
            }

            item { DsDivider() }

            // ── Level threshold ───────────────────────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = DsTheme.dimens.md, vertical = DsTheme.dimens.sm),
                    verticalArrangement = Arrangement.spacedBy(DsTheme.dimens.sm),
                ) {
                    DsText("Level", style = DsTextStyle.LabelMedium)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.xs)) {
                        item {
                            SelectableChip(
                                label = "All",
                                selected = state.pauseOnLevelAtLeast == null,
                                onClick = { DebugStepper.setPauseOnLevelAtLeast(null) },
                            )
                        }
                        items(enumValues<LogLevel>()) { level ->
                            SelectableChip(
                                label = level.name,
                                selected = state.pauseOnLevelAtLeast == level,
                                onClick = { DebugStepper.setPauseOnLevelAtLeast(level) },
                            )
                        }
                    }
                }
            }
        }

        item { DsDivider() }

        // ── Auto-resume ───────────────────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DsTheme.dimens.md, vertical = DsTheme.dimens.sm),
                verticalArrangement = Arrangement.spacedBy(DsTheme.dimens.sm),
            ) {
                DsText("Auto-resume", style = DsTextStyle.LabelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.xs)) {
                    items(autoResumeOptions) { seconds ->
                        SelectableChip(
                            label = if (seconds == null) "Off" else "${seconds}s",
                            selected = state.autoResumeSeconds == seconds,
                            onClick = { DebugStepper.setAutoResumeSeconds(seconds) },
                        )
                    }
                }
            }
        }

        // ── Caught ────────────────────────────────────────────────────────────
        if (state.steppedEvents.isEmpty()) return@LazyColumn

        item { DsDivider() }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DsTheme.dimens.md, vertical = DsTheme.dimens.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(DsTheme.dimens.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DsText("Caught", style = DsTextStyle.LabelMedium)
                    DsText(
                        text = "${state.steppedEvents.size}",
                        style = DsTextStyle.LabelSmall,
                        color = DsTheme.colors.content3,
                    )
                }
                DsText(
                    text = "Clear",
                    style = DsTextStyle.LabelSmall,
                    color = DsTheme.colors.danger,
                    modifier = Modifier.clickable { DebugStepper.clearSteppedEvents() },
                )
            }
        }

        items(state.steppedEvents.asReversed(), key = { it.id }) { log ->
            renderer.Item(
                log = log,
                onClick = { navigator?.push(ConsoleRoute.LogDetail("", log.id)) },
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
    val bg = if (selected) DsTheme.colors.primary else DsTheme.colors.background3
    val fg = if (selected) DsTheme.colors.primaryContent else DsTheme.colors.content3

    Box(
        modifier = Modifier
            .clip(DsTheme.rounding.xs)
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = DsTheme.dimens.sm, vertical = DsTheme.dimens.xxs),
    ) {
        DsText(label, style = DsTextStyle.LabelSmall, color = fg)
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
        DsText(label, style = DsTextStyle.Body)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
