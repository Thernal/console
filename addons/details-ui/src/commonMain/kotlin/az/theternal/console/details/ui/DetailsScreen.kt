package az.theternal.console.details.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import az.theternal.console.ui.designsystem.foundation.theme.DsPreview
import az.theternal.console.details.ConsoleDetails
import az.theternal.console.ui.designsystem.components.core.DsDivider
import az.theternal.console.ui.designsystem.components.provider.ThemeProvider
import az.theternal.console.ui.designsystem.components.core.DsText
import az.theternal.console.ui.designsystem.foundation.theme.Theme

@Composable
internal fun DetailsScreen() {
    val details by ConsoleDetails.flow.collectAsState()

    if (details.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            DsText(
                text = "No details registered",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp16),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Theme.rounding.r12)
                .background(Theme.colors.background2)
                .border(Theme.metrics.borderWidth, Theme.colors.border, Theme.rounding.r12),
        ) {
            Column {
                details.entries.forEachIndexed { index, (key, value) ->
                    if (index > 0) DsDivider()
                    DetailRow(label = key, value = value)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Theme.dimens.dp12, vertical = Theme.dimens.dp10),
        horizontalArrangement = Arrangement.spacedBy(Theme.dimens.dp16),
        verticalAlignment = Alignment.Top,
    ) {
        DsText(
            text = label,
            style = Theme.typography.label02,
            color = Theme.colors.content04,
            modifier = Modifier.weight(0.38f),
        )
        Box(
            modifier = Modifier.weight(0.62f),
            contentAlignment = Alignment.TopEnd,
        ) {
            DsText(
                text = value,
                style = Theme.typography.body02,
                color = Theme.colors.content01,
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewDetailRow() {
    ThemeProvider {
        DetailRow(label = "User ID", value = "abc-123-def-456")
    }
}

@DsPreview
@Composable
private fun PreviewDetailRowLong() {
    ThemeProvider {
        DetailRow(label = "Base URL", value = "https://api.example.com/v1/users/profile")
    }
}
