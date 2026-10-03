package io.thernal.console.inspector.ui.overlay

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HeatColorTest {
    @Test
    fun `counts at or above the threshold saturate to the same color`() {
        assertEquals(heatColor(count = 30, threshold = 30), heatColor(count = 500, threshold = 30))
    }

    @Test
    fun `colder and hotter counts differ`() {
        assertNotEquals(heatColor(count = 1, threshold = 30), heatColor(count = 30, threshold = 30))
    }

    @Test
    fun `a non positive threshold does not divide by zero`() {
        assertEquals(heatColor(count = 5, threshold = 1), heatColor(count = 5, threshold = 0))
    }
}
