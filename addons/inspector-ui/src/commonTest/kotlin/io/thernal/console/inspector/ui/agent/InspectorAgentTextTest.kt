package io.thernal.console.inspector.ui.agent

import androidx.compose.ui.unit.IntSize
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class InspectorAgentTextTest {
    private val lines = listOf(
        line(id = 1, depth = 0, name = "HomeScreen"),
        line(id = 2, depth = 1, name = "CartList", file = "Cart.kt"),
        line(id = 3, depth = 2, name = "CartRow", file = "Cart.kt"),
        line(id = 4, depth = 3, name = "Price"),
        line(id = 5, depth = 1, name = "Footer"),
    )

    @Test
    fun `a query keeps each match with what is inside it and starts it at depth 0`() {
        val rows = selectRows(lines, query = "cartrow", depth = null)

        assertEquals(listOf(3 to 0, 4 to 1), rows.map { it.id to it.depth })
    }

    @Test
    fun `depth cuts the tree below the top and a match in a matched subtree is not restarted`() {
        assertEquals(listOf(1, 2, 5), selectRows(lines, query = null, depth = 1).map { it.id })
        assertEquals(listOf(2, 3, 4), selectRows(lines, query = "Cart.kt", depth = null).map { it.id })
    }

    @Test
    fun `a line shows id name file bounds texts and recompositions and the tree says what it left out`() {
        val row = line(id = 12, depth = 1, name = "CartRow", file = "Cart.kt")
            .copy(bounds = "16,320 360×56", texts = listOf("Total"), recompositions = 3)

        assertEquals("  #12 CartRow (Cart.kt) @16,320 360×56 \"Total\" ×3", uiLine(row))
        assertEquals("Tree\n#1 HomeScreen\n… 4 more nodes; narrow with `query` or `depth`", treeText("Tree", lines, 1))
    }

    @Test
    fun `node ids are read from commas spaces and hashes each once`() {
        assertEquals(listOf(12, 15, 20), nodeIds(" 12, #15  20;12 "))
        assertEquals(emptyList(), nodeIds(null))
        assertEquals(emptyList(), nodeIds("  "))
    }

    @Test
    fun `a token that is not an id is refused with what to pass`() {
        val error = assertFailsWith<IllegalArgumentException> { nodeIds("12, CartRow") }

        assertTrue(error.message.orEmpty().contains("\"CartRow\" is not a node id"))
    }

    @Test
    fun `a snapshot answer gives the screen in dp and px then the image and the marked nodes`() {
        val marked = line(id = 12, depth = 0, name = "CartRow", file = "Cart.kt").copy(bounds = "16,320 360×56")

        val text = snapshotText(
            screen = IntSize(width = 1206, height = 2622),
            density = 3f,
            image = IntSize(width = 736, height = 1600),
            scale = 0.61f,
            marked = listOf(marked),
            notes = listOf("#99: no node with this id on screen now."),
        )

        val lines = text.lines()
        assertEquals("Screen 402×874 dp (1206×2622 px, 3.0 px per dp); image 736×1600 px, 61%.", lines[0])
        assertTrue(lines[1].startsWith("Marked ("))
        val expected = listOf("#12 CartRow (Cart.kt) @16,320 360×56", "#99: no node with this id on screen now.")
        assertEquals(expected, lines.drop(2))
    }

    @Test
    fun `a plain snapshot has no marked section`() {
        val text = snapshotText(
            screen = IntSize(width = 800, height = 600),
            density = 1f,
            image = IntSize(width = 800, height = 600),
            scale = 1f,
            marked = emptyList(),
            notes = emptyList(),
        )

        assertEquals("Screen 800×600 dp (800×600 px, 1.0 px per dp); image 800×600 px, 100%.", text)
    }

    private fun line(
        id: Int,
        depth: Int,
        name: String,
        file: String? = null,
    ): UiNodeLine {
        return UiNodeLine(id, depth, name, file, bounds = null, recompositions = 0, texts = emptyList())
    }
}
