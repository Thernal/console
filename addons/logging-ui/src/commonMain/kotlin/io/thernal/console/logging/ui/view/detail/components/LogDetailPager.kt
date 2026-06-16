package io.thernal.console.logging.ui.view.detail.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.core.log.Log

@Composable
fun LogDetailPager(
    pagerState: PagerState,
    logs: State<List<Log>>,
    renderer: LogRenderer,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageSpacing = Theme.dimens.dp12,
        userScrollEnabled = logs.value.size > 1,
    ) { pageIndex ->
        logs.value.getOrNull(pageIndex)?.let { pageLog ->
            val scrollState = remember(pageLog.id) { ScrollState(initial = 0) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            ) {
                renderer.Detail(log = pageLog)
            }
        }
    }
}
