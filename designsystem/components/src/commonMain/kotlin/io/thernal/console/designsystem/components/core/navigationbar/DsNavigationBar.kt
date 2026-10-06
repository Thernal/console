package io.thernal.console.designsystem.components.core.navigationbar

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme

/** Share of the edge fade drawn fully opaque, so the cut-off item reads as hidden, not just dimmed. */
private const val FADE_SOLID_STOP = 0.3f

/**
 * Bottom navigation bar whose items all get the same width. While every item fits at its natural
 * width they share the bar equally; once they do not, the bar scrolls horizontally at that width
 * and a fade on each scrollable edge says there is more.
 */
@Composable
fun DsNavigationBar(
    modifier: Modifier = Modifier,
    containerColor: Color = Theme.colors.background2,
    content: @Composable () -> Unit,
) {
    val scrollState = rememberScrollState()
    val minItemWidth = Theme.metrics.minTouchTarget
    val fadeWidth = Theme.dimens.dp48

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(containerColor)
            .windowInsetsPadding(
                insets = WindowInsets.safeDrawing.only(
                    sides = WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal,
                ),
            )
            .scrollFade(
                scrollState = scrollState,
                color = containerColor,
                width = fadeWidth,
            ),
    ) {
        val viewportWidth = constraints.maxWidth

        Layout(
            content = content,
            modifier = Modifier
                .horizontalScroll(scrollState)
                .padding(vertical = Theme.dimens.dp4),
        ) { measurables, _ ->
            if (measurables.isEmpty()) return@Layout layout(0, 0) {}

            val naturalWidth = measurables
                .maxOf { it.maxIntrinsicWidth(Constraints.Infinity) }
                .coerceAtLeast(minItemWidth.roundToPx())
            val sharedWidth = viewportWidth / measurables.size
            val itemWidth = if (sharedWidth >= naturalWidth) sharedWidth else naturalWidth

            val placeables = measurables.map { it.measure(Constraints.fixedWidth(itemWidth)) }
            layout(itemWidth * placeables.size, placeables.maxOf { it.height }) {
                placeables.forEachIndexed { index, placeable ->
                    placeable.placeRelative(x = itemWidth * index, y = 0)
                }
            }
        }
    }
}

private fun Modifier.scrollFade(
    scrollState: ScrollState,
    color: Color,
    width: Dp,
): Modifier = drawWithContent {
    drawContent()
    val fadeWidth = width.toPx().coerceAtMost(size.width / 2)
    if (scrollState.canScrollBackward) {
        drawRect(
            brush = Brush.horizontalGradient(
                0f to color,
                FADE_SOLID_STOP to color,
                1f to Color.Transparent,
                startX = 0f,
                endX = fadeWidth,
            ),
            size = Size(fadeWidth, size.height),
        )
    }
    if (scrollState.canScrollForward) {
        drawRect(
            brush = Brush.horizontalGradient(
                0f to Color.Transparent,
                1f - FADE_SOLID_STOP to color,
                1f to color,
                startX = size.width - fadeWidth,
                endX = size.width,
            ),
            topLeft = Offset(size.width - fadeWidth, 0f),
            size = Size(fadeWidth, size.height),
        )
    }
}

@DsPreview
@Composable
private fun PreviewDsNavigationBar() {
    ThemeProvider {
        DsNavigationBar {
            DsNavigationBarItem(
                selected = true,
                onClick = {},
                icon = { DsIcon(icon = Icons.AutoMirrored.Outlined.List) },
                label = { DsText(text = "Logs") },
            )
            DsNavigationBarItem(
                selected = false,
                onClick = {},
                icon = { DsIcon(icon = Icons.Outlined.Search) },
                label = { DsText(text = "Search") },
            )
        }
    }
}

@DsPreview
@Composable
private fun PreviewDsNavigationBarScrollable() {
    val items: List<Pair<String, ImageVector>> = listOf(
        "Logs" to Icons.AutoMirrored.Outlined.List,
        "Stepper" to Icons.Outlined.BugReport,
        "Details" to Icons.Outlined.Info,
        "Crashes" to Icons.Outlined.BugReport,
        "Inspector" to Icons.Outlined.Search,
        "Network" to Icons.Outlined.Info,
        "Storage" to Icons.Outlined.Info,
        "Performance" to Icons.Outlined.Search,
        "Settings" to Icons.Outlined.Settings,
    )
    ThemeProvider {
        DsNavigationBar {
            items.forEachIndexed { index, (title, icon) ->
                DsNavigationBarItem(
                    selected = index == 0,
                    onClick = {},
                    icon = { DsIcon(icon = icon) },
                    label = { DsText(text = title) },
                )
            }
        }
    }
}
