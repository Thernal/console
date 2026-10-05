package io.thernal.console.inspector.ui.overlay

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HeatColorsTest {
    private val heat = HeatColors(cold = Color.Blue, warm = Color.Yellow, hot = Color.Red)

    @Test
    fun `counts at or above the threshold saturate to the same color`() {
        assertEquals(heat.colorFor(count = 30, threshold = 30), heat.colorFor(count = 500, threshold = 30))
    }

    @Test
    fun `colder and hotter counts differ`() {
        assertNotEquals(heat.colorFor(count = 1, threshold = 30), heat.colorFor(count = 30, threshold = 30))
    }

    @Test
    fun `a non positive threshold does not divide by zero`() {
        assertEquals(heat.colorFor(count = 5, threshold = 1), heat.colorFor(count = 5, threshold = 0))
    }
}
