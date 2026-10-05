package io.thernal.console.inspector.ui.engine.recomposition

import kotlin.test.Test
import kotlin.test.assertEquals

class InspectorTimingTest {
    @Test
    fun `an empty timing has a zero average`() {
        assertEquals(0L, InspectorTiming().averageMicros)
    }

    @Test
    fun `plus accumulates count total max and last`() {
        val timing = InspectorTiming().plus(100L).plus(300L).plus(200L)

        assertEquals(3, timing.count)
        assertEquals(200L, timing.averageMicros)
        assertEquals(300L, timing.maxMicros)
        assertEquals(200L, timing.lastMicros)
    }
}
