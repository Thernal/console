package io.thernal.console.sample.screens.brew

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.sample.components.DemoScaffold
import io.thernal.console.sample.theme.Background
import io.thernal.console.sample.theme.Brand
import io.thernal.console.sample.theme.Muted
import io.thernal.console.sample.theme.OnSurface
import io.thernal.console.sample.theme.Outline
import io.thernal.console.sample.theme.Surface
import io.thernal.console.sample.theme.SurfaceElevated

private val ORDER_STEPS = listOf(
    OrderStage.Validating,
    OrderStage.Reserving,
    OrderStage.Charging,
    OrderStage.Confirming,
)

@Composable
fun BrewScreen(
    onBack: () -> Unit,
    viewModel: BrewViewModel = viewModel { BrewViewModel() },
) {
    val stage by viewModel.stage

    DemoScaffold(title = "Brew checkout", onBack = onBack) {
        OrderCard(items = viewModel.cart, total = viewModel.total)
        Timeline(stage = stage)
        PlaceOrderButton(
            stage = stage,
            total = viewModel.total,
            onPlace = viewModel::placeOrder,
            onReset = viewModel::reset,
        )
    }
}

@Composable
private fun OrderCard(
    items: List<CartItem>,
    total: String,
) {
    Card {
        Text(text = "Your order", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))
        items.forEach { item ->
            LineItem(item)
            Spacer(Modifier.height(14.dp))
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Outline),
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Total", color = OnSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = total, color = OnSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LineItem(item: CartItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceElevated),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = item.emoji, fontSize = 22.sp)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, color = OnSurface, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Text(text = item.detail, color = Muted, fontSize = 13.sp)
        }
        Text(text = item.price, color = OnSurface, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun Timeline(stage: OrderStage) {
    Card {
        Text(text = "Order status", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(16.dp))
        ORDER_STEPS.forEachIndexed { index, step ->
            val done = stage == OrderStage.Done || stage.ordinal > step.ordinal
            val active = stage.ordinal == step.ordinal
            TimelineRow(
                label = step.label,
                done = done,
                active = active,
                isLast = index == ORDER_STEPS.lastIndex,
            )
        }
    }
}

@Composable
private fun TimelineRow(
    label: String,
    done: Boolean,
    active: Boolean,
    isLast: Boolean,
) {
    val dotColor by animateColorAsState(if (done || active) Brand else SurfaceElevated)
    val labelColor by animateColorAsState(
        when {
            done -> OnSurface
            active -> Brand
            else -> Muted
        },
    )

    Row(verticalAlignment = Alignment.Top) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(dotColor),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    done -> Text("✓", color = OnSurface, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    active -> CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        color = OnSurface,
                        strokeWidth = 2.dp,
                    )

                    else -> Box(Modifier.size(8.dp).clip(CircleShape).background(Muted))
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(22.dp)
                        .background(if (done) Brand else Outline),
                )
            }
        }
        Spacer(Modifier.width(14.dp))
        Text(
            text = label,
            color = labelColor,
            fontSize = 15.sp,
            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = 3.dp),
        )
    }
}

@Composable
private fun PlaceOrderButton(
    stage: OrderStage,
    total: String,
    onPlace: () -> Unit,
    onReset: () -> Unit,
) {
    val working = stage != OrderStage.Idle && stage != OrderStage.Done

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = onPlace,
            enabled = !working && stage != OrderStage.Done,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Brand,
                contentColor = Background,
                disabledContainerColor = SurfaceElevated,
                disabledContentColor = OnSurface,
            ),
        ) {
            AnimatedContent(targetState = stage) { current ->
                when {
                    current == OrderStage.Idle ->
                        Text("Place order · $total", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                    current == OrderStage.Done ->
                        Text("Order placed ✓", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)

                    else -> Row(verticalAlignment = Alignment.CenterVertically) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = OnSurface,
                            strokeWidth = 2.dp,
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(current.label + "…", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        if (stage == OrderStage.Done) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onReset) {
                Text("Place another", color = Muted, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun Card(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .border(1.dp, Outline, RoundedCornerShape(20.dp))
            .padding(20.dp),
        content = content,
    )
}
