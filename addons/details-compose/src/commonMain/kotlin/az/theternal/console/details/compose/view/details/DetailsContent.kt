package az.theternal.console.details.compose.view.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.compose.core.preview
import az.theternal.console.details.compose.view.details.components.DetailRow
import az.theternal.console.details.compose.view.details.model.DetailsState
import az.theternal.console.designsystem.components.core.DsCard
import az.theternal.console.designsystem.components.core.DsDivider
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.components.provider.ThemeProvider
import az.theternal.console.designsystem.foundation.theme.DsPreview
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun DetailsContent(state: DetailsState) {
    if (state.details.value.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            DsText(
                text = "No details registered",
                style = Theme.typography.body02,
                color = Theme.colors.content03,
            )
        }
        return
    }
    DetailsFilled(details = state.details)
}

@Composable
private fun DetailsFilled(details: State<Map<String, String>>) {
    val entries = details.value.entries.toList()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                horizontal = Theme.metrics.screenPaddingHorizontal,
                vertical = Theme.metrics.screenPaddingVertical,
            ),
    ) {
        DsCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                entries.forEachIndexed { index, entry ->
                    if (index > 0) DsDivider()
                    DetailRow(label = entry.key, value = entry.value)
                }
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewDetailsContentEmpty() {
    ThemeProvider {
        DetailsContent(state = DetailsState())
    }
}

@DsPreview
@Composable
private fun PreviewDetailsContentFilled() {
    val state = DetailsState().preview {
        state.details.set(
            mapOf(
                "User ID" to "usr-abc-123",
                "Environment" to "production",
                "App Version" to "2.4.1",
                "Build" to "1047",
            ),
        )
    }
    ThemeProvider {
        DetailsContent(state = state)
    }
}
