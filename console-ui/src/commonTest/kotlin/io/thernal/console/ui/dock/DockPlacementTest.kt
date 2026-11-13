package io.thernal.console.ui.dock

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize
import io.thernal.console.api.addon.ConsoleDockEdge
import io.thernal.console.api.addon.ConsoleDockVertical
import kotlin.test.Test
import kotlin.test.assertEquals

class DockPlacementTest {
    private val bubble = IntSize(BUBBLE, BUBBLE)
    private val card = IntSize(CARD_WIDTH, CARD_HEIGHT)

    @Test
    fun `named spots put the box flush with the edge at that height`() {
        val leftBottom = offsetFor(ConsoleDockEdge.Left, ConsoleDockVertical.Bottom.fraction, bubble, WIDTH, HEIGHT)
        val rightCenter = offsetFor(ConsoleDockEdge.Right, ConsoleDockVertical.Center.fraction, bubble, WIDTH, HEIGHT)

        assertEquals(Offset(-(WIDTH - BUBBLE), HEIGHT - BUBBLE), leftBottom)
        assertEquals(Offset(0f, (HEIGHT - BUBBLE) / 2), rightCenter)
    }

    @Test
    fun `a box flush with the left and bottom edges stays there when it grows and shrinks`() {
        val folded = Offset(-(WIDTH - BUBBLE), HEIGHT - BUBBLE)

        val opened = resizedOffset(folded, bubble, card, WIDTH, HEIGHT)
        val foldedAgain = resizedOffset(opened, card, bubble, WIDTH, HEIGHT)

        assertEquals(Offset(-(WIDTH - CARD_WIDTH), HEIGHT - CARD_HEIGHT), opened)
        assertEquals(folded, foldedAgain)
    }

    @Test
    fun `a box away from the left and bottom edges keeps its offset when it resizes`() {
        val middle = Offset(MIDDLE_X, MIDDLE_Y)

        assertEquals(middle, resizedOffset(middle, bubble, card, WIDTH, HEIGHT))
        assertEquals(Offset.Zero, resizedOffset(Offset.Zero, IntSize.Zero, bubble, WIDTH, HEIGHT))
    }

    @Test
    fun `a move reports the edge and height it reached`() {
        val viewModel = DockViewModel()
        viewModel.dispatch(DockIntent.SizeChanged(bubble, WIDTH, HEIGHT))

        viewModel.dispatch(DockIntent.MoveTo(ConsoleDockEdge.Left, ConsoleDockVertical.Bottom, WIDTH, HEIGHT))
        val layout = dockLayout(viewModel.state, selectedWidgetId = "stepper", WIDTH, HEIGHT, Offset(0f, TOP_INSET))

        assertEquals(ConsoleDockEdge.Left, layout?.edge)
        assertEquals(1f, layout?.verticalFraction)
        assertEquals(0, layout?.left)
        assertEquals((TOP_INSET + HEIGHT - BUBBLE).toInt(), layout?.top)
    }

    private companion object {
        const val WIDTH = 1000f
        const val HEIGHT = 2000f
        const val BUBBLE = 100
        const val CARD_WIDTH = 600
        const val CARD_HEIGHT = 800
        const val MIDDLE_X = -300f
        const val MIDDLE_Y = 500f
        const val TOP_INSET = 80f
    }
}
