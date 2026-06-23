package io.thernal.console.sample.screens.counter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.sample.components.DemoScaffold
import io.thernal.console.sample.theme.Brand
import io.thernal.console.sample.theme.Muted
import io.thernal.console.sample.theme.OnSurface
import io.thernal.console.sample.theme.Outline
import io.thernal.console.sample.theme.Surface

@Composable
fun CounterScreen(
    onBack: () -> Unit,
    viewModel: CounterViewModel = viewModel { CounterViewModel() },
) {
    val count by viewModel.count

    DemoScaffold(title = "Counter", onBack = onBack) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Surface)
                .border(1.dp, Outline, RoundedCornerShape(24.dp))
                .padding(vertical = 48.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = count.toString(), color = OnSurface, fontSize = 72.sp, fontWeight = FontWeight.Bold)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        ) {
            StepButton(symbol = "−", onClick = viewModel::decrement)
            StepButton(symbol = "+", onClick = viewModel::increment)
        }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            TextButton(onClick = viewModel::reset) {
                Text("Reset", color = Muted, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun StepButton(
    symbol: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(72.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Brand, contentColor = Color.Black),
    ) {
        Text(text = symbol, fontSize = 28.sp, fontWeight = FontWeight.Bold)
    }
}
