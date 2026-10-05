package io.thernal.console.inspector.ui.engine.geometry

import androidx.compose.ui.geometry.Rect
import io.thernal.console.inspector.ui.engine.geometry.ModifierKind.Other
import io.thernal.console.inspector.ui.engine.geometry.ModifierKind.Padding
import io.thernal.console.inspector.ui.engine.geometry.ModifierKind.Visual
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class NodeBoxTest {
    private val outer = Rect(0f, 0f, 200f, 100f)
    private val border = Rect(8f, 8f, 192f, 92f)
    private val content = Rect(24f, 24f, 176f, 76f)

    @Test
    fun `the frame is the first visual modifier`() {
        assertEquals(1, frameIndex(listOf(Padding, Visual, Padding)))
        assertEquals(0, frameIndex(listOf(Visual, Padding)))
    }

    @Test
    fun `with nothing drawn the frame is the outermost box`() {
        assertEquals(0, frameIndex(listOf(Padding, Other)))
        assertNull(frameIndex(emptyList()))
    }

    @Test
    fun `padding before the background is margin and padding after it is inside`() {
        // padding(8).background().padding(16): the background sits on the box after the margin.
        val box = nodeBox(chain = listOf(outer, border, border, content), kinds = listOf(Padding, Visual, Padding))!!

        assertEquals(border, box.frame)
        assertEquals(8f, box.margin!!.left)
        assertEquals(16f, box.padding!!.left)
    }

    @Test
    fun `a background then a padding frames the padding inside`() {
        // background().padding(16): the gap to a neighbour is measured from the background, not the content.
        val box = nodeBox(chain = listOf(border, border, content), kinds = listOf(Visual, Padding))!!

        assertEquals(border, box.frame)
        assertNull(box.margin)
        assertEquals(16f, box.padding!!.top)
    }

    @Test
    fun `without modifiers the frame is the content`() {
        val box = nodeBox(chain = listOf(content), kinds = emptyList())!!

        assertEquals(content, box.frame)
        assertNull(box.margin)
        assertNull(box.padding)
    }
}
