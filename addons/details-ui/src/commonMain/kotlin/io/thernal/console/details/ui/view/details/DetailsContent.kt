package io.thernal.console.details.ui.view.details

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
import io.thernal.console.ui.core.preview
import io.thernal.console.details.ui.view.details.components.DetailRow
import io.thernal.console.details.ui.view.details.model.DetailsState
import io.thernal.console.designsystem.components.core.DsContainer
import io.thernal.console.designsystem.components.core.DsDivider
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

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

    DetailsContainer(details = state.details)
}

@Composable
private fun DetailsContainer(details: State<Map<String, String>>) {
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
        DsContainer(modifier = Modifier.fillMaxWidth()) {
            Column {
                entries.forEachIndexed { index, entry ->
                    if (index > 0) {
                        DsDivider()
                    }

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
