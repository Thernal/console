package io.thernal.console.designsystem.components.core.tabview

import androidx.compose.runtime.Immutable
import kotlin.math.abs

@Immutable
data class DsTabItemState(
    val index: Int,
    val isSelected: Boolean,
    val selectionProgress: Float,
) {
    companion object {
        fun create(
            index: Int,
            currentPage: Int,
            pagerProgress: Float,
        ): DsTabItemState {
            return DsTabItemState(
                index = index,
                isSelected = currentPage == index,
                selectionProgress = (1f - abs(pagerProgress - index)).coerceIn(0f, 1f),
            )
        }
    }
}
