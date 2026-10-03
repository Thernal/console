package io.thernal.console.ui.dock

import kotlin.test.Test
import kotlin.test.assertEquals

class DockSnapTest {
    private val maxWidth = 1000f
    private val width = 100f

    @Test
    fun `a box on the right half settles against the right edge`() {
        assertEquals(0f, snapToHorizontalEdge(offsetX = -100f, width = width, maxWidth = maxWidth))
    }

    @Test
    fun `a box on the left half settles against the left edge`() {
        assertEquals(-900f, snapToHorizontalEdge(offsetX = -700f, width = width, maxWidth = maxWidth))
    }

    @Test
    fun `a box exactly in the middle goes right`() {
        assertEquals(0f, snapToHorizontalEdge(offsetX = -450f, width = width, maxWidth = maxWidth))
    }

    @Test
    fun `a box as wide as the screen does not move`() {
        assertEquals(0f, snapToHorizontalEdge(offsetX = 0f, width = maxWidth, maxWidth = maxWidth))
    }
}
