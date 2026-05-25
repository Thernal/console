package az.theternal.console.designsystem.components.core.collapsible

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun DsCollapsible(
    modifier: Modifier = Modifier,
    state: DsCollapsibleState = rememberDsCollapsibleState(),
    header: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(state.topAppBarState)

    Column(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clipToBounds()
                .layout { measurable, constraints ->
                    val offset = state.topAppBarState.heightOffset
                    val visible = (state.headerHeightPx + offset).coerceAtLeast(0f).toInt()
                    val adjusted = if (state.headerHeightPx > 0f) {
                        constraints.copy(minHeight = visible, maxHeight = visible)
                    } else {
                        constraints
                    }
                    val placeable = measurable.measure(adjusted)
                    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
                },
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(unbounded = true)
                    .onGloballyPositioned { coords ->
                        state.onHeaderMeasured(coords.size.height.toFloat())
                    },
            ) {
                header()
            }
        }

        content()
    }
}
