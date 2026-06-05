package io.thernal.console.designsystem.components.core.tabview

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun rememberDsTabState(
    itemCount: Int,
    initialPage: Int = 0,
): DsTabState {
    val lastPageIndex = (itemCount - 1).coerceAtLeast(0)
    val pagerState = rememberPagerState(
        initialPage = initialPage.coerceIn(0, lastPageIndex),
    ) { itemCount }
    val coroutineScope = rememberCoroutineScope()

    val state = remember(pagerState, coroutineScope) {
        DsTabState(
            itemCount = itemCount,
            pagerState = pagerState,
            coroutineScope = coroutineScope,
        )
    }

    state.itemCount = itemCount

    LaunchedEffect(itemCount, state.lastPageIndex) {
        if (!state.hasItems) return@LaunchedEffect

        val coercedPage = pagerState.currentPage.coerceIn(0, state.lastPageIndex)
        if (coercedPage != pagerState.currentPage) {
            state.scrollToPage(coercedPage)
        }
    }

    return state
}
