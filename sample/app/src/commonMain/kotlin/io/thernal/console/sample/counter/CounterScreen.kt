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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun CounterScreen(vm: CounterViewModel = viewModel { CounterViewModel() }) {
    val count by vm.count

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "$count",
            style = MaterialTheme.typography.displayLarge,
        )
        Spacer(Modifier.height(32.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = vm::decrement) {
                Text("-", style = MaterialTheme.typography.titleLarge)
            }
            Button(onClick = vm::increment) {
                Text("+", style = MaterialTheme.typography.titleLarge)
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = vm::reset) {
            Text("Reset")
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(onClick = vm::logMultiline) {
            Text("Log multiline")
        }
        Spacer(Modifier.height(48.dp))
        Text(
            text = "Swipe up then down to open console",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}
