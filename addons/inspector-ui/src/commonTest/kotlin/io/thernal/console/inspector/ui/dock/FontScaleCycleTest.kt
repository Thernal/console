package io.thernal.console.inspector.ui.dock

import io.thernal.console.inspector.InspectorFontScale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FontScaleCycleTest {
    @Test
    fun `cycling starts from follow system with the first scale`() {
        assertEquals(InspectorFontScale.entries.first(), nextFontScale(null))
    }

    @Test
    fun `cycling steps through every scale in order`() {
        val entries = InspectorFontScale.entries

        assertEquals(entries[1], nextFontScale(entries[0]))
    }

    @Test
    fun `cycling past the last scale returns to follow system`() {
        assertNull(nextFontScale(InspectorFontScale.entries.last()))
    }
}
