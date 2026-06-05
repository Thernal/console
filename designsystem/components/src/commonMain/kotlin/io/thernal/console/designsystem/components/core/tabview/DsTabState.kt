package io.thernal.console.designsystem.components.core.tabview

import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Stable
class DsTabState internal constructor(
    itemCount: Int,
    val pagerState: PagerState,
    private val coroutineScope: CoroutineScope,
) {
    var itemCount by mutableIntStateOf(itemCount)
        internal set

    val hasItems: Boolean
        get() = itemCount > 0

    val lastPageIndex: Int
        get() = (itemCount - 1).coerceAtLeast(0)

    val currentPage: Int
        get() = pagerState.currentPage.coerceIn(0, lastPageIndex)

    private val currentPageOffsetFraction: Float
        get() = pagerState.currentPageOffsetFraction

    val pagerProgress: Float
        get() = (pagerState.currentPage + currentPageOffsetFraction)
            .coerceIn(0f, lastPageIndex.toFloat())

    val indicatorState: DsTabIndicatorState
        get() = DsTabIndicatorState(
            pagerProgress = pagerProgress,
            itemCount = itemCount,
        )

    fun tabItemState(index: Int): DsTabItemState {
        return DsTabItemState.create(
            index = index,
            currentPage = currentPage,
            pagerProgress = pagerProgress,
        )
    }

    fun indicatorOffset(tabWidthPx: Int): Int {
        return (tabWidthPx * pagerProgress).toInt()
    }

    fun onTabClick(index: Int) {
        if (!hasItems) return

        coroutineScope.launch {
            animateScrollToPage(index)
        }
    }

    suspend fun animateScrollToPage(index: Int) {
        if (!hasItems) return
        pagerState.animateScrollToPage(index.coerceIn(0, lastPageIndex))
    }

    suspend fun scrollToPage(index: Int) {
        if (!hasItems) return
        pagerState.scrollToPage(index.coerceIn(0, lastPageIndex))
    }
}
