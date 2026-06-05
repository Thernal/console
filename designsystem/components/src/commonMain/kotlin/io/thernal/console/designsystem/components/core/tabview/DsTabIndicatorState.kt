package io.thernal.console.designsystem.components.core.tabview

import androidx.compose.runtime.Immutable

@Immutable
data class DsTabIndicatorState(
    val pagerProgress: Float,
    val itemCount: Int,
)
