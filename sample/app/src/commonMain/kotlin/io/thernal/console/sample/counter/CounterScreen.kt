package io.thernal.console.sample.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CounterScreen(viewModel: CounterViewModel = viewModel { CounterViewModel() }) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CountText(viewModel.count)
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = viewModel::decrement) {
                Text("-", style = MaterialTheme.typography.titleLarge)
            }
            Button(onClick = viewModel::increment) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = viewModel::reset) {
            Text("Reset")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = viewModel::logMultiline) {
            Text("Log multiline")
        }
        Spacer(Modifier.height(8.dp))
        SampleNetworkButton(
            onClick = viewModel::fetchSampleTodo,
            isLoading = viewModel.isRequestLoading,
            text = "Fetch fake todo",
        )
        Spacer(Modifier.height(8.dp))
        SampleNetworkButton(
            onClick = viewModel::createSamplePost,
            isLoading = viewModel.isPostRequestLoading,
            text = "Create fake post",
        )
        Spacer(Modifier.height(48.dp))
        Text(
            text = "Swipe up, down, left and right to open console",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun CountText(count: State<Int>) {
    Text(
        text = "${count.value}",
        style = MaterialTheme.typography.displayLarge,
    )
}

@Composable
private fun SampleNetworkButton(
    onClick: () -> Unit,
    isLoading: State<Boolean>,
    text: String,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = !isLoading.value,
    ) {
        Text(if (isLoading.value) "Loading..." else text)
    }
}
