package io.thernal.console.inspector.ui.engine

import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NodePaddingTest {
    private val outer = Rect(0f, 0f, 200f, 100f)
    private val content = Rect(16f, 8f, 184f, 92f)

    @Test
    fun `one padding is the gap between its box and the next`() {
        val rings = paddingRings(chain = listOf(outer, content), isPadding = listOf(true))

        assertEquals(1, rings.size)
        assertEquals(16f, rings.single().left)
        assertEquals(8f, rings.single().top)
        assertEquals(16f, rings.single().right)
        assertEquals(8f, rings.single().bottom)
    }

    @Test
    fun `other modifiers in between are skipped over`() {
        val sized = Rect(16f, 8f, 184f, 92f)
        val rings = paddingRings(chain = listOf(outer, sized, content), isPadding = listOf(true, false))

        assertEquals(sized, rings.single().inner)
    }

    @Test
    fun `stacked paddings add up`() {
        val middle = Rect(10f, 10f, 190f, 90f)
        val padding = NodePadding(
            paddingRings(chain = listOf(outer, middle, content), isPadding = listOf(true, true)),
        )

        assertEquals(2, padding.rings.size)
        assertEquals(16f, padding.left)
        assertEquals(8f, padding.top)
    }

    @Test
    fun `a padding of zero is dropped and an unmeasured box is ignored`() {
        assertTrue(paddingRings(chain = listOf(content, content), isPadding = listOf(true)).isEmpty())
        assertTrue(paddingRings(chain = listOf(null, content), isPadding = listOf(true)).isEmpty())
    }

    @Test
    fun `the description lists the sides that have padding in dp`() {
        val padding = NodePadding(paddingRings(listOf(outer, content), listOf(true)))

        assertEquals("top 4 · left 8 · right 8 · bottom 4 dp", padding.describe(density = 2f))
        assertNull(NodePadding(emptyList()).describe(density = 2f))
    }
}
