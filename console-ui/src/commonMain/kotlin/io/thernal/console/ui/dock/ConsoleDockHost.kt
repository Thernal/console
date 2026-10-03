package io.thernal.console.ui.dock

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.addon.ConsoleDockStatus
import io.thernal.console.designsystem.foundation.theme.Theme
import kotlin.math.roundToInt

private val dockElevation = 4.dp
private val roundCorner = 22.dp
private val cardCorner = 14.dp
private const val MORPH_FADE_MS = 140

/**
 * The shared floating container for dock widgets. Resting it is a round button. When a widget needs attention and the
 * dock is folded, it grows into a pill with that widget's actions; an open card stays open until the user folds it. Tapping the button opens a card with one tab per widget. The
 * container morphs between the three, so it reads as one object.
 */
@Composable
internal fun ConsoleDockHost() {
    val widgets = shownDockWidgets()
    if (widgets.isEmpty()) return

    val viewModel = viewModel { DockViewModel() }
    val state = viewModel.state
    val attentionWidgets = widgets.filter { it.status() == ConsoleDockStatus.Attention }
    val needsAttention = attentionWidgets.isNotEmpty()

    val mode = when {
        !state.isMinimized.value -> DockMode.Card
        needsAttention -> DockMode.Pill
        else -> DockMode.Bubble
    }
    val selected = widgets.firstOrNull { it.id == state.selectedId.value } ?: widgets.first()

    BoxWithConstraints(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeContent)) {
        val maxWidthPx = constraints.maxWidth.toFloat()
        val maxHeightPx = constraints.maxHeight.toFloat()
        val availableWidth = maxWidth
        val availableHeight = maxHeight
        val isCard = mode == DockMode.Card
        val dragModifier = Modifier.dockDrag(viewModel, maxWidthPx, maxHeightPx, snapToEdge = !isCard)
        val offsetX by animateFloatAsState(
            targetValue = state.offsetX.value,
            animationSpec = if (state.isDragging.value) snap() else spring(),
            label = "dock-offset-x",
        )
        val corner by animateDpAsState(
            targetValue = if (isCard) cardCorner else roundCorner,
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
            label = "dock-corner",
        )
        val shape = RoundedCornerShape(corner)
        val borderColor = if (needsAttention) Theme.colors.warning else Theme.colors.border

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset { IntOffset(offsetX.roundToInt(), state.offsetY.value.roundToInt()) }
                .shadow(dockElevation, shape)
                .clip(shape)
                .background(Theme.colors.background1)
                .border(Theme.metrics.borderWidth, borderColor, shape)
                .onSizeChanged { viewModel.dispatch(DockIntent.SizeChanged(it, maxWidthPx, maxHeightPx)) }
                .then(if (isCard) Modifier else dragModifier),
        ) {
            AnimatedContent(
                targetState = mode,
                transitionSpec = {
                    val enter = fadeIn(tween(MORPH_FADE_MS, delayMillis = MORPH_FADE_MS / 2))
                    val exit = fadeOut(tween(MORPH_FADE_MS / 2))
                    val resize = SizeTransform(clip = true) { _, _ -> spring(stiffness = Spring.StiffnessMediumLow) }
                    enter.togetherWith(exit).using(resize)
                },
                label = "dock-mode",
            ) { target ->
                when (target) {
                    DockMode.Bubble -> DockBubble(
                        widgets = widgets,
                        onClick = { viewModel.dispatch(DockIntent.SetMinimized(false)) },
                    )

                    DockMode.Pill -> DockPill(
                        widgets = widgets,
                        attentionWidgets = attentionWidgets,
                        onOpen = { viewModel.dispatch(DockIntent.SetMinimized(false)) },
                    )

                    DockMode.Card -> DockCard(
                        widgets = widgets,
                        selected = selected,
                        maxWidth = availableWidth,
                        maxHeight = availableHeight,
                        gripModifier = dragModifier,
                        onIntent = viewModel::dispatch,
                    )
                }
            }
        }
    }
}
