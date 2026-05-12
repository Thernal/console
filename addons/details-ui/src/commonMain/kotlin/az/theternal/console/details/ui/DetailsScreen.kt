package az.theternal.console.details.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import az.theternal.console.details.ConsoleDetails
import az.theternal.console.ui.designsystem.foundation.theme.Theme
import az.theternal.console.ui.designsystem.components.core.DsDivider
import az.theternal.console.ui.designsystem.components.core.DsText

@Composable
internal fun DetailsScreen() {
    val details by ConsoleDetails.flow.collectAsState()

    if (details.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            DsText("No details registered", style = Theme.typography.body02)
        }
        return
    }

    LazyColumn(Modifier.fillMaxSize()) {
        items(details.entries.toList(), key = { it.key }) { (key, value) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Theme.dimens.dp16, vertical = Theme.dimens.dp12),
                verticalAlignment = Alignment.Top,
            ) {
                DsText(
                    text = key,
                    style = Theme.typography.label01,
                    color = Theme.colors.primary01,
                    modifier = Modifier.weight(0.35f),
                )
                DsText(
                    text = value,
                    style = Theme.typography.body02,
                    modifier = Modifier.weight(0.65f),
                )
            }
            DsDivider()
        }
    }
}
