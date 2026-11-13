package io.thernal.console.inspector.ui.agent

import kotlin.test.Test
import kotlin.test.assertEquals

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

    private fun line(
        id: Int,
        depth: Int,
        name: String,
        file: String? = null,
    ): UiNodeLine {
        return UiNodeLine(id, depth, name, file, bounds = null, recompositions = 0, texts = emptyList())
    }
}
