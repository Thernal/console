package io.thernal.console.stepper.ui.dock

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import io.thernal.console.api.addon.ConsoleDockStatus
import io.thernal.console.api.addon.ConsoleDockWidget
import io.thernal.console.stepper.ui.view.overlay.model.StepperOverlayViewModel

/** The stepper's place in the console dock. A paused pipeline keeps it on screen, whatever is hidden. */
internal object StepperDockWidget : ConsoleDockWidget {
    override val id = "stepper"
    override val title = "Stepper"
    override val icon: ImageVector = Icons.Outlined.PlaylistPlay

    @Composable
    override fun status(): ConsoleDockStatus {
        val state = viewModel { StepperOverlayViewModel() }.state
        return when {
            !state.isEnabled.value -> ConsoleDockStatus.Idle
            state.isPaused.value -> ConsoleDockStatus.Attention
            else -> ConsoleDockStatus.Active
        }
    }

    @Composable
    override fun summary(): String {
        val state = viewModel { StepperOverlayViewModel() }.state
        return stepperSummary(
            isEnabled = state.isEnabled.value,
            isPaused = state.isPaused.value,
            statusText = state.statusText.value,
            heldCount = state.caughtCount.value,
        )
    }

    @Composable
    override fun QuickAction() {
        StepperQuickAction()
    }

    @Composable
    override fun Panel() {
        StepperPanel()
    }
}
