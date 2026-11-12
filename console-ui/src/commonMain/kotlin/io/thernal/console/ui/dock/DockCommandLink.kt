package io.thernal.console.ui.dock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.geometry.Offset
import io.thernal.console.api.addon.ConsoleDock
import io.thernal.console.api.addon.ConsoleDockCommand
import io.thernal.console.core.ConsoleInternalApi

/**
 * Connects the dock on screen to `ConsoleDock`: runs the commands sent to it as the dock's own intents, so clamping
 * and snapping stay in [DockViewModel], and reports the layout back until the dock leaves the screen. [isMoving] reads
 * the snapshot state that tells whether the dock is still on its way, so a reader can wait for a move to finish.
 */
@OptIn(ConsoleInternalApi::class)
@Composable
internal fun DockCommandLink(
    viewModel: DockViewModel,
    selectedWidgetId: String,
    maxWidthPx: Float,
    maxHeightPx: Float,
    origin: Offset,
    isMoving: () -> Boolean,
) {
    val selected by rememberUpdatedState(selectedWidgetId)
    val maxWidth by rememberUpdatedState(maxWidthPx)
    val maxHeight by rememberUpdatedState(maxHeightPx)
    val windowOrigin by rememberUpdatedState(origin)
    val moving by rememberUpdatedState(isMoving)

    LaunchedEffect(viewModel) {
        ConsoleDock.commands.collect { command ->
            when (command) {
                is ConsoleDockCommand.MoveTo -> viewModel.dispatch(
                    DockIntent.MoveTo(command.edge, command.vertical, maxWidth, maxHeight),
                )

                is ConsoleDockCommand.SetMinimized -> viewModel.dispatch(DockIntent.SetMinimized(command.isMinimized))

                is ConsoleDockCommand.SelectWidget -> {
                    viewModel.dispatch(DockIntent.SelectWidget(command.widgetId))
                    viewModel.dispatch(DockIntent.SetMinimized(false))
                }
            }
        }
    }
    LaunchedEffect(viewModel) {
        snapshotFlow {
            val layout = dockLayout(viewModel.state, selected, maxWidth, maxHeight, windowOrigin)
            layout?.copy(isMoving = moving())
        }.collect(ConsoleDock::reportLayout)
    }
    DisposableEffect(viewModel) {
        onDispose { ConsoleDock.reportLayout(null) }
    }
}
