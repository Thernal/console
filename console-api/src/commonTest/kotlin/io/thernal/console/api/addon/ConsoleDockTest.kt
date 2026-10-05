package io.thernal.console.api.addon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ConsoleDockTest {
    private class FakeWidget(
        override val id: String,
        override val title: String = id,
        override val order: Int = Int.MAX_VALUE,
    ) : ConsoleDockWidget {
        override val icon: ImageVector = ImageVector.Builder(
            name = id,
            defaultWidth = 1.dp,
            defaultHeight = 1.dp,
            viewportWidth = 1f,
            viewportHeight = 1f,
        ).build()

        @Composable
        override fun Panel() {
        }
    }

    @Test
    fun `a visible dock shows widgets that are not hidden`() {
        val config = ConsoleDockConfig(isVisible = true, hiddenWidgetIds = setOf("inspector"))

        assertTrue(config.shows(id = "stepper", needsAttention = false))
        assertFalse(config.shows(id = "inspector", needsAttention = false))
    }

    @Test
    fun `hiding the dock hides everything that does not need attention`() {
        val config = ConsoleDockConfig(isVisible = false)

        assertFalse(config.shows(id = "stepper", needsAttention = false))
        assertFalse(config.shows(id = null, needsAttention = false))
    }

    @Test
    fun `a widget that needs attention is shown even when hidden`() {
        val config = ConsoleDockConfig(isVisible = false, hiddenWidgetIds = setOf("stepper"))

        assertTrue(config.shows(id = "stepper", needsAttention = true))
    }

    @Test
    fun `widgets are kept in order and ids stay unique`() {
        val late = FakeWidget(id = "dock-test-late", order = 90)
        val early = FakeWidget(id = "dock-test-early", order = 80)

        ConsoleDock.register(late)
        ConsoleDock.register(early)

        val ids = ConsoleDock.widgets.map { it.id }
        assertTrue(ids.indexOf("dock-test-early") < ids.indexOf("dock-test-late"))
        assertFailsWith<IllegalStateException> { ConsoleDock.register(FakeWidget(id = "dock-test-late")) }
    }

    @Test
    fun `widgets with the same order are sorted by title and not by who registered first`() {
        val zulu = FakeWidget(id = "dock-test-zulu", title = "Zulu", order = 70)
        val alpha = FakeWidget(id = "dock-test-alpha", title = "alpha", order = 70)

        ConsoleDock.register(zulu)
        ConsoleDock.register(alpha)

        val ids = ConsoleDock.widgets.map { it.id }
        assertTrue(ids.indexOf("dock-test-alpha") < ids.indexOf("dock-test-zulu"))
    }

    @Test
    fun `registering the same widget twice keeps one entry`() {
        val widget = FakeWidget(id = "dock-test-once")

        ConsoleDock.register(widget)
        ConsoleDock.register(widget)

        assertEquals(1, ConsoleDock.widgets.count { it.id == "dock-test-once" })
    }

    @Test
    fun `layered overlays are ordered within their layer`() {
        val high = ConsoleOverlay(layer = ConsoleOverlayLayer.Backdrop, order = 5) {}
        val low = ConsoleOverlay(layer = ConsoleOverlayLayer.Backdrop, order = -5) {}

        ConsoleOverlays.register(high)
        ConsoleOverlays.register(low)

        val backdrop = ConsoleOverlays.layer(ConsoleOverlayLayer.Backdrop)
        assertTrue(backdrop.indexOf(low) < backdrop.indexOf(high))
    }
}
