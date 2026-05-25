package az.theternal.console.details.compose.view.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import az.theternal.console.details.ConsoleDetails
import az.theternal.console.details.compose.view.details.components.DetailRow
import az.theternal.console.designsystem.components.core.DsCard
import az.theternal.console.designsystem.components.core.DsDivider
import az.theternal.console.designsystem.components.core.DsText
import az.theternal.console.designsystem.foundation.theme.Theme

@Composable
internal fun DetailsView() {
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
            .padding(
                horizontal = Theme.metrics.screenPaddingHorizontal,
                vertical = Theme.metrics.screenPaddingVertical,
            ),
    ) {
        DsCard(modifier = Modifier.fillMaxWidth()) {
            Column {
                details.entries.forEachIndexed { index, (key, value) ->
                    if (index > 0) DsDivider()
                    DetailRow(label = key, value = value)
                }
            }
        }
    }
}
