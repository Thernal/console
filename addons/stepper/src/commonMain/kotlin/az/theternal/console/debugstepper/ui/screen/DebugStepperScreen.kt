package az.theternal.console.debugstepper.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import az.theternal.console.debugstepper.DebugStepper
import az.theternal.console.ui.designsystem.DsTheme
import az.theternal.console.ui.ds.DsDivider
import az.theternal.console.ui.ds.DsText
import az.theternal.console.ui.ds.DsTextStyle

@Composable
internal fun DebugStepperScreen() {
    val state by DebugStepper.state.collectAsState()

    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                ToggleRow(
                    label = "Enable stepper",
                    checked = state.enabled,
                    onCheckedChange = { DebugStepper.setEnabled(it) },
                )
                if (state.enabled) {
                    ToggleRow(
                        label = "Pause on each log",
                        checked = state.paused,
                        onCheckedChange = { DebugStepper.setPaused(it) },
                    )
                }
            }
        }

        if (state.events.isNotEmpty()) {
            item { DsDivider() }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DsText("Recent events", style = DsTextStyle.LabelMedium)
                    DsText(
                        text = "${state.events.size}",
                        style = DsTextStyle.LabelMedium,
                        color = DsTheme.colors.content3,
                    )
                }
            }
            items(state.events.asReversed(), key = { it.id }) { log ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                ) {
                    if (log.tag != null) {
                        DsText(
                            text = log.tag!!,
                            style = DsTextStyle.LabelMedium,
                            color = DsTheme.colors.primary,
                        )
                    }
                    DsText(
                        text = log.message,
                        style = DsTextStyle.Body,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                DsDivider()
            }
        }
    }
}

@Composable
private fun ToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DsText(label, style = DsTextStyle.Body)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
