package io.thernal.console.stepper.ui.dock

import kotlin.test.Test
import kotlin.test.assertEquals

class StepperSummaryTest {
    @Test
    fun `a disabled stepper reads off whatever the status says`() {
        assertEquals(
            "Off",
            stepperSummary(isEnabled = false, isPaused = true, statusText = "Running", heldCount = 4),
        )
    }

    @Test
    fun `an enabled stepper shows its status`() {
        assertEquals(
            "Running",
            stepperSummary(isEnabled = true, isPaused = false, statusText = "Running", heldCount = 0),
        )
    }

    @Test
    fun `held events are counted next to the status`() {
        assertEquals(
            "Paused · 3 held",
            stepperSummary(isEnabled = true, isPaused = true, statusText = "Paused", heldCount = 3),
        )
    }

    @Test
    fun `a paused pipeline with nothing waiting reads paused and not idle`() {
        assertEquals(
            "Paused",
            stepperSummary(isEnabled = true, isPaused = true, statusText = "Idle", heldCount = 0),
        )
    }
}
