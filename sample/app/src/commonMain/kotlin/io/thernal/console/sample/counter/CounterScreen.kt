package io.thernal.console.sample.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.runtime.log.LogLevel

private val DEMO_LOG_LEVELS = listOf(
    LogLevel.Debug,
    LogLevel.Info,
    LogLevel.Success,
    LogLevel.Warning,
    LogLevel.Error,
)

@Composable
fun SampleScreen(viewModel: SampleViewModel = viewModel { SampleViewModel() }) {
    val isFetchingTodo by viewModel.isFetchingTodo
    val isCreatingPost by viewModel.isCreatingPost

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(all = 20.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        Text(
            text = "Swipe ↑↓←→ to open the console",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )

        // Console.notify — fire-and-forget from any thread; level drives color in the log list
        DemoSection(title = "Log Levels") {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                DEMO_LOG_LEVELS.forEach { level ->
                    OutlinedButton(onClick = { viewModel.logAtLevel(level) }) {
                        Text(level.name)
                    }
                }
            }
        }

        // Console.asyncNotify — suspends until the observer processes each event; order is preserved
        DemoSection(title = "Async Ordering") {
            Button(onClick = viewModel::logOrdered) {
                Text("Log ${SampleViewModel.ORDERED_LOG_COUNT} steps in order")
            }
        }

        // ConsoleDetails.put — live key/value updates visible in the Details tab
        DemoSection(title = "Details Panel") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::setSessionInfo) {
                    Text("Set session info")
                }
                OutlinedButton(onClick = viewModel::clearDetails) {
                    Text("Clear")
                }
            }
        }

        // ConsoleNetworkKtorPlugin — installed once on the HttpClient; captures every request automatically
        DemoSection(title = "Network (Ktor)") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = viewModel::fetchTodo,
                    enabled = !isFetchingTodo,
                ) {
                    Text(if (isFetchingTodo) "Fetching…" else "GET /todos/1")
                }
                Button(
                    onClick = viewModel::createPost,
                    enabled = !isCreatingPost,
                ) {
                    Text(if (isCreatingPost) "Posting…" else "POST /posts")
                }
            }
        }
    }
}

@Composable
private fun DemoSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
        )
        content()
    }
}
