package io.thernal.console.sample.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.thernal.console.sample.navigation.SampleRoute
import io.thernal.console.sample.theme.Background
import io.thernal.console.sample.theme.Brand
import io.thernal.console.sample.theme.Muted
import io.thernal.console.sample.theme.OnSurface
import io.thernal.console.sample.theme.Outline
import io.thernal.console.sample.theme.Surface
import io.thernal.console.sample.theme.SurfaceElevated

private data class Demo(
    val route: SampleRoute,
    val emoji: String,
    val title: String,
    val subtitle: String,
)

private val DEMOS = listOf(
    Demo(SampleRoute.Counter, "🔢", "Counter", "A tiny screen that logs every change"),
    Demo(SampleRoute.Brew, "☕", "Brew checkout", "A stepped order flow — great with the stepper"),
    Demo(SampleRoute.Playground, "🧪", "Playground", "Every console feature, one button at a time"),
)

@Composable
fun HomeScreen(onNavigate: (SampleRoute) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(text = "Console", color = OnSurface, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Brand.copy(alpha = 0.14f))
                    .padding(horizontal = 14.dp, vertical = 7.dp),
            ) {
                Text(
                    text = "Swipe ▲ ▼ ◀ ▶ to open the console",
                    color = Brand,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        Text(text = "Demos", color = Muted, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)

        DEMOS.forEach { demo ->
            DemoCard(demo = demo, onClick = { onNavigate(demo.route) })
        }
    }
}

@Composable
private fun DemoCard(
    demo: Demo,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Surface)
            .border(1.dp, Outline, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceElevated),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = demo.emoji, fontSize = 24.sp)
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = demo.title, color = OnSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(text = demo.subtitle, color = Muted, fontSize = 13.sp)
        }
        Spacer(Modifier.width(12.dp))
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Brand,
            modifier = Modifier.size(24.dp),
        )
    }
}
