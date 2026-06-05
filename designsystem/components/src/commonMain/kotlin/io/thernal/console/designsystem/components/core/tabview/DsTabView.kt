package io.thernal.console.designsystem.components.core.tabview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.thernal.console.designsystem.components.core.chip.DsChip
import io.thernal.console.designsystem.components.modifier.pressable
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

@Composable
fun DsTabView(
    state: DsTabState,
    modifier: Modifier = Modifier,
    labels: List<String> = emptyList(),
    tab: (@Composable (DsTabItemState) -> Unit)? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(
        space = Theme.dimens.dp8,
    ),
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        contentPadding = contentPadding,
    ) {
        items(
            count = state.itemCount,
        ) { index ->
            val tabItemState = state.tabItemState(index)
            Box(
                modifier = Modifier.pressable(
                    onPress = { state.onTabClick(index) },
                ),
            ) {
                if (tab != null) {
                    tab(tabItemState)
                } else {
                    DsChip(
                        label = labels.getOrElse(index) { "" },
                        selected = tabItemState.isSelected,
                    )
                }
            }
        }
    }
}

@DsPreview
@Composable
private fun PreviewDsTabView() {
    ThemeProvider {
        val titles = listOf("Request", "Response")
        val state = rememberDsTabState(itemCount = titles.size)

        DsTabView(
            state = state,
            labels = titles,
        )
    }
}
