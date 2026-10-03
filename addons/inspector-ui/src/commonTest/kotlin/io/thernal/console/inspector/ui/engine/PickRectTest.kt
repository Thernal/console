package io.thernal.console.inspector.ui.engine

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals

class PickRectTest {
    private val content = Rect(20f, 20f, 120f, 70f)
    private val outer = Rect(0f, 0f, 140f, 90f)

    @Test
    fun `a touch inside the content never needs the outer box`() {
        val area = pickRect(content, Offset(50f, 40f)) { error("not needed") }

        assertEquals(content, area)
    }

    @Test
    fun `a touch in the padding hits the whole box`() {
        val area = pickRect(content, Offset(5f, 40f)) { outer }

        assertEquals(outer, area)
    }

    @Test
    fun `a touch far away is left to other nodes without a layout query`() {
        val area = pickRect(content, Offset(2000f, 40f)) { error("not needed") }

        assertEquals(content, area)
    }

    @Test
    fun `a node without an outer box falls back to its content`() {
        assertEquals(content, pickRect(content, Offset(5f, 40f)) { null })
    }
}
