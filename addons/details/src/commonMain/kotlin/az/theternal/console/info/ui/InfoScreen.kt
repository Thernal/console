package az.theternal.console.info.ui

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
import androidx.compose.ui.unit.dp
import az.theternal.console.info.ConsoleDetails
import az.theternal.console.ui.designsystem.DsTheme
import az.theternal.console.ui.ds.DsDivider
import az.theternal.console.ui.ds.DsText
import az.theternal.console.ui.ds.DsTextStyle

@Composable
internal fun InfoScreen() {
    val details by ConsoleDetails.flow.collectAsState()

    if (details.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            DsText("No details registered", style = DsTextStyle.Body)
        }
        return
    }

    LazyColumn(Modifier.fillMaxSize()) {
        items(details.entries.toList(), key = { it.key }) { (key, value) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                DsText(
                    text = key,
                    style = DsTextStyle.LabelMedium,
                    color = DsTheme.colors.primary,
                    modifier = Modifier.weight(0.35f),
                )
                DsText(
                    text = value,
                    style = DsTextStyle.Body,
                    modifier = Modifier.weight(0.65f),
                )
            }
            DsDivider()
        }
    }
}
