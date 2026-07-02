package io.thernal.console.designsystem.components.core.swipeaction

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.unit.Dp.Companion.Unspecified

@Composable
@Suppress("LongMethod")
internal fun rememberSwipeActionState(
    itemId: Any? = null,
    enabled: Boolean = true,
    leadingAction: (@Composable BoxScope.(progress: Float) -> Unit)? = null,
    trailingAction: (@Composable BoxScope.(progress: Float) -> Unit)? = null,
): SwipeActionState {
    val coordinator = LocalSwipeActionCoordinator.current

    val snapAnimationSpec: AnimationSpec<Float> = remember {
        spring(stiffness = Spring.StiffnessMediumLow)
    }

    val anchorState = remember { AnchoredDraggableState(SwipeActionAnchor.CLOSED) }
    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = anchorState,
        animationSpec = snapAnimationSpec,
    )

    var leadingWidthPx by remember { mutableFloatStateOf(0f) }
    var trailingWidthPx by remember { mutableFloatStateOf(0f) }
    var contentHeight by remember { mutableStateOf(Unspecified) }
    var offsetX by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(leadingWidthPx, trailingWidthPx, leadingAction, trailingAction) {
        val hasLeading = leadingAction != null && leadingWidthPx > 0f
        val hasTrailing = trailingAction != null && trailingWidthPx > 0f

        anchorState.updateAnchors(
            DraggableAnchors {
                SwipeActionAnchor.CLOSED at 0f
                if (hasLeading) SwipeActionAnchor.LEADING_OPEN at leadingWidthPx
                if (hasTrailing) SwipeActionAnchor.TRAILING_OPEN at -trailingWidthPx
            },
        )
    }

    LaunchedEffect(anchorState) {
        snapshotFlow {
            try {
                anchorState.requireOffset()
            } catch (_: IllegalStateException) {
                0f
            }
        }.collect { offsetX = it }
    }

    val leadingProgress by remember(offsetX, leadingWidthPx, leadingAction) {
        derivedStateOf {
            if (leadingAction == null || leadingWidthPx <= 0f) {
                0f
            } else {
                (offsetX / leadingWidthPx).coerceIn(0f, 1f)
            }
        }
    }
    val trailingProgress by remember(offsetX, trailingWidthPx, trailingAction) {
        derivedStateOf {
            if (trailingAction == null || trailingWidthPx <= 0f) {
                0f
            } else {
                (-offsetX / trailingWidthPx).coerceIn(0f, 1f)
            }
        }
    }

    val currentId = coordinator?.focusedItemId
    LaunchedEffect(currentId, enabled) {
        if (!enabled) return@LaunchedEffect
        val shouldClose = currentId == null || currentId != itemId
        if (shouldClose && anchorState.currentValue != SwipeActionAnchor.CLOSED) {
            anchorState.animateTo(
                targetValue = SwipeActionAnchor.CLOSED,
                animationSpec = snapAnimationSpec,
            )
        }
    }

    LaunchedEffect(anchorState.currentValue, enabled) {
        if (!enabled) return@LaunchedEffect
        if (anchorState.currentValue == SwipeActionAnchor.LEADING_OPEN ||
            anchorState.currentValue == SwipeActionAnchor.TRAILING_OPEN
        ) {
            coordinator?.updateFocused(itemId)
        }
    }

    return SwipeActionState(
        anchorState = anchorState,
        flingBehavior = flingBehavior,
        setLeadingWidth = { leadingWidthPx = it },
        setTrailingWidth = { trailingWidthPx = it },
        setContentHeight = { contentHeight = it },
        leadingProgress = leadingProgress,
        trailingProgress = trailingProgress,
        contentHeight = contentHeight,
        offsetX = offsetX,
    )
}
