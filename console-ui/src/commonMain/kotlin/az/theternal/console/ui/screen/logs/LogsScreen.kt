package az.theternal.console.ui.screen.logs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import az.theternal.console.core.Console

private const val MILLIS_PER_SECOND = 1000L
private const val SECONDS_PER_MINUTE = 60L
private const val SECONDS_PER_HOUR = 3600L
private const val HOURS_PER_DAY = 24L

@Composable
internal fun LogsScreen(onNavigateToLogDetail: (groupId: String, logId: String) -> Unit) {
    val logs by Console.logObserver.logs.collectAsState()

    if (logs.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No logs yet", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    LazyColumn(Modifier.fillMaxSize()) {
        items(logs.asReversed(), key = { it.id }) { log ->
            Column(
                Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToLogDetail("", log.id) }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                if (log.tag != null) {
                    Text(
                        text = log.tag!!,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = log.message,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = formatTimestamp(log.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HorizontalDivider()
        }
    }
}

private fun formatTimestamp(epochMillis: Long): String {
    val totalSeconds = epochMillis / MILLIS_PER_SECOND
    val hh = totalSeconds / SECONDS_PER_HOUR % HOURS_PER_DAY
    val mm = totalSeconds / SECONDS_PER_MINUTE % SECONDS_PER_MINUTE
    val ss = totalSeconds % SECONDS_PER_MINUTE
    return "${hh.toString().padStart(2, '0')}:${mm.toString().padStart(2, '0')}:${ss.toString().padStart(2, '0')} UTC"
}
