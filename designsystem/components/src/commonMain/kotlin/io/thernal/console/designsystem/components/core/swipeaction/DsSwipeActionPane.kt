package io.thernal.console.designsystem.components.core.swipeaction

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import io.thernal.console.designsystem.components.core.DsIcon
import io.thernal.console.designsystem.components.core.DsIconButton
import io.thernal.console.designsystem.components.core.DsText
import io.thernal.console.designsystem.components.modifier.applyIf
import io.thernal.console.designsystem.components.provider.ThemeProvider
import io.thernal.console.designsystem.foundation.theme.DsPreview
import io.thernal.console.designsystem.foundation.theme.Theme
import kotlin.math.roundToInt

/**
 * Container that lets list rows or cards expose leading/trailing actions when swiped horizontally.
 * Wrap the list in a [DsSwipeActionHost] so at most one row stays open at a time.
 *
 * Prefer the simpler overload (`leading` / `trailing`) unless you genuinely need the progress —
 * for example, to fade or zoom an action as the swipe advances. Progress-aware slots recompose on
 * every drag frame to deliver that Float, so treat them as an opt-in and default to the
 * progressless API for best performance.
 *
 * ```kotlin
 * DsSwipeActionPane(
 *     itemId = item.id,
 *     trailingWithProgress = { progress -> DeleteAction(progress) },
 * ) {
 *     SwipeableRowContent(item)
 * }
 * ```
 */
@Composable
fun DsSwipeActionPane(
    modifier: Modifier = Modifier,
    itemId: Any? = null,
    enabled: Boolean = true,
    leadingWithProgress: (@Composable BoxScope.(progress: Float) -> Unit)? = null,
    trailingWithProgress: (@Composable BoxScope.(progress: Float) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = rememberSwipeActionState(
        itemId = itemId,
        enabled = enabled,
        leadingAction = leadingWithProgress,
        trailingAction = trailingWithProgress,
    )

    val density = LocalDensity.current

    val actionHeightModifier = Modifier.applyIf(state.contentHeight != Dp.Unspecified) {
        requiredHeight(state.contentHeight)
    }

    Box(modifier = modifier) {
        if (leadingWithProgress != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .onSizeChanged { size -> state.setLeadingWidth(size.width.toFloat()) }
                    .wrapContentWidth()
                    .then(actionHeightModifier),
            ) {
                leadingWithProgress(state.leadingProgress)
            }
        }

        if (trailingWithProgress != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .onSizeChanged { size -> state.setTrailingWidth(size.width.toFloat()) }
                    .wrapContentWidth()
                    .then(actionHeightModifier),
            ) {
                trailingWithProgress(state.trailingProgress)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(state.offsetX.roundToInt(), 0) },
        ) {
            Box(
                modifier = Modifier
                    .anchoredDraggable(
                        state = state.anchorState,
                        orientation = Orientation.Horizontal,
                        enabled = enabled,
                        flingBehavior = state.flingBehavior,
                    )
                    .onSizeChanged { size ->
                        state.setContentHeight(with(density) { size.height.toDp() })
                    },
            ) {
                content()
            }
        }
    }
}

@Composable
fun DsSwipeActionPane(
    modifier: Modifier = Modifier,
    itemId: Any? = null,
    enabled: Boolean = true,
    leading: (@Composable BoxScope.() -> Unit)? = null,
    trailing: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    DsSwipeActionPane(
        itemId = itemId,
        modifier = modifier,
        enabled = enabled,
        leadingWithProgress = leading?.let { action -> { _ -> action() } },
        trailingWithProgress = trailing?.let { action -> { _ -> action() } },
        content = content,
    )
}

@DsPreview
@Composable
private fun PreviewDsSwipeActionPane() {
    ThemeProvider {
        DsSwipeActionHost {
            DsSwipeActionPane(
                itemId = "preview",
                trailing = {
                    DsIconButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(horizontal = Theme.dimens.dp8),
                    ) {
                        DsIcon(
                            icon = Icons.Outlined.Delete,
                            color = Theme.colors.danger,
                        )
                    }
                },
            ) {
                DsText(
                    text = "Swipe me left",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Theme.dimens.dp16),
                )
            }
        }
    }
}
