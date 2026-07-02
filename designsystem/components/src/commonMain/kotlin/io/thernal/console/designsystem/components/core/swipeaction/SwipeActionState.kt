package io.thernal.console.designsystem.components.core.swipeaction

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.ui.unit.Dp

internal data class SwipeActionState(
    val anchorState: AnchoredDraggableState<SwipeActionAnchor>,
    val flingBehavior: FlingBehavior,
    val setLeadingWidth: (Float) -> Unit,
    val setTrailingWidth: (Float) -> Unit,
    val leadingProgress: Float,
    val trailingProgress: Float,
    val offsetX: Float,
    val contentHeight: Dp,
    val setContentHeight: (Dp) -> Unit,
)
