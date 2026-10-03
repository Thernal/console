package io.thernal.console.inspector.ui.engine

import kotlin.test.Test
import kotlin.test.assertEquals

class NodeDetailsTextTest {
    private fun details(
        recompositions: Int,
        own: Int,
    ) = NodeDetails(
        name = "DemoCard",
        definedIn = null,
        parentName = null,
        boundsPx = null,
        sizePx = null,
        sizeDp = null,
        positionDp = null,
        padding = null,
        density = null,
        layoutDirection = null,
        recompositions = recompositions,
        ownRecompositions = own,
        skippedPasses = null,
        lastReason = null,
        modifiers = emptyList(),
        parameterNames = emptyList(),
        slotValues = emptyList(),
    )

    @Test
    fun `equal counts read as one number`() {
        assertEquals("20", details(recompositions = 20, own = 20).recompositionText())
    }

    @Test
    fun `framework recompositions below the node are named separately`() {
        assertEquals("20 own · 40 with framework below", details(recompositions = 40, own = 20).recompositionText())
    }
}
