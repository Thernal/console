package io.thernal.console.sample.screens.playground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.core.log.LogLevel
import io.thernal.console.sample.components.DemoScaffold
import io.thernal.console.sample.theme.Muted

private val DEMO_LOG_LEVELS = listOf(
    LogLevel.Debug,
    LogLevel.Info,
    LogLevel.Success,
    LogLevel.Warning,
    LogLevel.Error,
)

@Composable
fun PlaygroundScreen(
    onBack: () -> Unit,
    viewModel: PlaygroundViewModel = viewModel { PlaygroundViewModel() },
) {
    val isFetchingTodo by viewModel.isFetchingTodo
    val isCreatingPost by viewModel.isCreatingPost

    DemoScaffold(title = "Playground", onBack = onBack) {
        Section(title = "Log levels") {
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

        Section(title = "Async ordering") {
            Button(onClick = viewModel::logOrdered) {
                Text("Log ${PlaygroundViewModel.ORDERED_LOG_COUNT} steps in order")
            }
        }

        Section(title = "Details panel") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::setSessionInfo) { Text("Set session info") }
                OutlinedButton(onClick = viewModel::clearDetails) { Text("Clear") }
            }
        }

        Section(title = "Network (Ktor)") {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = viewModel::fetchTodo, enabled = !isFetchingTodo) {
                    Text(if (isFetchingTodo) "Fetching…" else "GET /todos/1")
                }
                Button(onClick = viewModel::createPost, enabled = !isCreatingPost) {
                    Text(if (isCreatingPost) "Posting…" else "POST /posts")
                }
            }
        }

        Section(title = "JSON body formatting") {
            Button(onClick = viewModel::logUnformattedJson) {
                Text("Emit unformatted JSON")
            }
        }

        Section(title = "Crash report") {
            Button(onClick = viewModel::crash) {
                Text("Crash the app")
            }
        }
    }
}

@Composable
private fun Section(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = title, color = Muted, fontSize = 13.sp)
        content()
    }
}
