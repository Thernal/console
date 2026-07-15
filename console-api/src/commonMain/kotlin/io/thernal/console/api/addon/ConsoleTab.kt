package io.thernal.console.api.addon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.vector.ImageVector

@Stable
interface ConsoleTab {
    val title: String
    val icon: ImageVector

    /**
     * Position in the bottom nav bar, ascending, ties broken by registration order (which is not
     * guaranteed — it differs by platform auto-init mechanism). Every tab MUST override this with
     * a distinct value; leaving the default risks an arbitrary tie with another tab. Existing
     * values, spaced by 10 for easy insertion: Logs 0, Stepper 10, Details 20, Crashes 30,
     * Settings [Int.MAX_VALUE] (always last).
     */
    val order: Int get() = Int.MAX_VALUE

    @Composable
    fun Content()

    /**
     * Optional actions contributed to the console app bar while this tab is selected.
     * Rendered in the app bar trailing area, before the close button. Empty by default.
     */
    @Composable
    fun Actions() {
    }
}
