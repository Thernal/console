package io.thernal.console.inspector.ui.engine

import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PickChainTest {
    private val screen = Rect(0f, 0f, 400f, 800f)
    private val list = Rect(0f, 100f, 400f, 800f)
    private val card = Rect(16f, 120f, 384f, 300f)
    private val text = Rect(32f, 136f, 200f, 160f)

    private val bounds = mapOf(
        "Root" to screen,
        "Scaffold" to screen,
        "List" to list,
        "Card" to card,
        "Surface" to card,
        "Text" to text,
    )

    /** Parent of each item; the chain is a single line of ancestors. */
    private val parents = mapOf("Scaffold" to "Root", "List" to "Scaffold", "Card" to "List", "Surface" to "Card")

    private val chain = PickChain.collapse(listOf("Root", "Scaffold", "List", "Card", "Surface", "Text")) { bounds[it] }

    private fun holds(
        outer: String,
        inner: String,
    ): Boolean = generateSequence(inner) { parents[it] }.any { it == outer }

    @Test
    fun `screen filling roots and same sized wrappers collapse away`() {
        assertEquals(listOf("List", "Card", "Text"), chain)
    }

    @Test
    fun `a single run is kept`() {
        assertEquals(listOf("Root"), PickChain.collapse(listOf("Root", "Scaffold")) { bounds[it] })
    }

    @Test
    fun `a first tap picks the outermost component`() {
        assertEquals("List", PickChain.stepInto(chain, null) { false })
    }

    @Test
    fun `tapping inside the selection steps one level in and stops at the innermost`() {
        assertEquals("Card", PickChain.stepInto(chain, "List") { holds(it, "List") })
        assertEquals("Text", PickChain.stepInto(chain, "Card") { holds(it, "Card") })
        assertEquals("Text", PickChain.stepInto(chain, "Text") { holds(it, "Text") })
    }

    @Test
    fun `tapping beside the selection picks on its level`() {
        val otherCard = listOf("List", "OtherCard", "OtherText")

        assertEquals("OtherCard", PickChain.stepInto(otherCard, "Card") { holds(it, "Card") })
    }

    @Test
    fun `tapping empty space around the selection picks the container`() {
        assertEquals("List", PickChain.stepInto(listOf("List"), "Card") { holds(it, "Card") })
    }

    @Test
    fun `nothing under the pointer picks nothing`() {
        assertNull(PickChain.stepInto(emptyList<String>(), "Card") { false })
    }
}
