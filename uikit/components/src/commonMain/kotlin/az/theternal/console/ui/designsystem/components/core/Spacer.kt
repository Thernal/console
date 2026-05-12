package az.theternal.console.ui.designsystem.components.core

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.layout.Spacer as LegacySpacer

@Composable
fun ColumnScope.Spacer(weight: Float = 1f) {
    LegacySpacer(modifier = Modifier.weight(weight))
}

@Composable
fun RowScope.Spacer(weight: Float = 1f) {
    LegacySpacer(modifier = Modifier.weight(weight))
}

@Composable
fun ColumnScope.Spacer(height: Dp) {
    LegacySpacer(modifier = Modifier.height(height))
}

@Composable
fun RowScope.Spacer(width: Dp) {
    LegacySpacer(modifier = Modifier.width(width))
}
