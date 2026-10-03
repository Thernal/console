package io.thernal.console.ui.dock

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.foundation.theme.Theme

private val fadeHeight = 24.dp
private const val SLIDE_DIVISOR = 6
private const val CONTENT_FADE_MS = 160

/**
 * The selected widget's live summary and controls. Switching tabs slides the content toward the new tab while the
 * card resizes with a spring. Taller than [maxHeight] it scrolls, and a fade says there is more.
 */
@Composable
internal fun DockContent(
    widgets: List<ConsoleDockWidget>,
    selected: ConsoleDockWidget,
    maxHeight: Dp,
) {
    val scrollState = rememberScrollState()
    val surface = Theme.colors.background1
    Box {
        Column(Modifier.heightIn(max = maxHeight).verticalScroll(scrollState)) {
            AnimatedContent(
                targetState = selected,
                transitionSpec = { tabTransition(widgets) },
                contentKey = { it.id },
                label = "dock-content",
            ) { widget ->
                Column(Modifier.fillMaxWidth().padding(Theme.dimens.dp10)) {
                    val summary = widget.summary()
                    if (summary != null) {
                        DsText(
                            text = summary,
                            style = Theme.typography.label02,
                            color = Theme.colors.content03,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(bottom = Theme.dimens.dp6),
                        )
                    }
                    widget.Panel()
                }
            }
        }
        if (scrollState.canScrollForward) {
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(fadeHeight)
                    .background(Brush.verticalGradient(listOf(Color.Transparent, surface))),
            )
        }
    }
}

private fun AnimatedContentTransitionScope<ConsoleDockWidget>.tabTransition(
    widgets: List<ConsoleDockWidget>,
): ContentTransform {
    val direction = if (widgets.indexOf(targetState) >= widgets.indexOf(initialState)) 1 else -1
    val enter = slideInHorizontally(tween(CONTENT_FADE_MS * 2)) { direction * it / SLIDE_DIVISOR }
        .plus(fadeIn(tween(CONTENT_FADE_MS, delayMillis = CONTENT_FADE_MS / 3)))
    val exit = slideOutHorizontally(tween(CONTENT_FADE_MS * 2)) { -direction * it / SLIDE_DIVISOR }
        .plus(fadeOut(tween(CONTENT_FADE_MS / 2)))
    val resize = SizeTransform(clip = true) { _, _ -> spring(stiffness = Spring.StiffnessMediumLow) }
    return enter.togetherWith(exit).using(resize)
}
