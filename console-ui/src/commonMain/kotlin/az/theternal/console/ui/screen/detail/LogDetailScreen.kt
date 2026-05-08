package az.theternal.console.ui.screen.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import az.theternal.console.core.Console

private const val MILLIS_PER_SECOND = 1000L
private const val SECONDS_PER_MINUTE = 60L
private const val SECONDS_PER_HOUR = 3600L
private const val HOURS_PER_DAY = 24L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LogDetailScreen(
    groupId: String,
    logId: String,
    onBack: () -> Unit,
) {
    val logs by Console.logObserver.logs.collectAsState()
    val log = logs.find { it.id == logId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(log?.tag ?: "Log Detail") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { padding ->
        if (log == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Log not found")
            }
        } else {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            ) {
                Text(
                    text = log.message,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(Modifier.height(16.dp))
                DetailRow("Tag", log.tag ?: "—")
                DetailRow("Group", groupId.ifBlank { "—" })
                DetailRow("Time (UTC)", formatTimestamp(log.timestamp))
                DetailRow("ID", log.id)
            }
        }
    }
}

@Composable
private fun DetailRow(
    label: String,
    value: String,
) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
    Spacer(Modifier.height(8.dp))
}

private fun formatTimestamp(epochMillis: Long): String {
    val totalSeconds = epochMillis / MILLIS_PER_SECOND
    val hh = totalSeconds / SECONDS_PER_HOUR % HOURS_PER_DAY
    val mm = totalSeconds / SECONDS_PER_MINUTE % SECONDS_PER_MINUTE
    val ss = totalSeconds % SECONDS_PER_MINUTE
    return "${hh.toString().padStart(2, '0')}:${mm.toString().padStart(2, '0')}:${ss.toString().padStart(2, '0')}"
}
