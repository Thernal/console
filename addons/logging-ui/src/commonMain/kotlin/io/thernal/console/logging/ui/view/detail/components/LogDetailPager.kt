package io.thernal.console.logging.ui.view.detail.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import io.thernal.console.api.ui.LogRenderer
import io.thernal.console.designsystem.foundation.theme.Theme
import io.thernal.console.runtime.log.Log

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
    ) { pageIndex ->
        logs.value.getOrNull(pageIndex)?.let { pageLog ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            ) {
                renderer.Detail(log = pageLog)
            }
        }
    }
}
