package io.thernal.console.agent.ui.addon

import io.thernal.console.api.addon.ConsoleDockConfig
import io.thernal.console.api.addon.ConsoleDockEdge
import io.thernal.console.api.addon.ConsoleDockLayout
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DockAgentTextTest {
    private val widgets = linkedMapOf("inspector" to "Inspector", "stepper" to "Stepper")

    @Test
    fun `the state names the spot the bounds and every widget with its hidden flag`() {
        val layout = ConsoleDockLayout(
            edge = ConsoleDockEdge.Left,
            verticalFraction = 1f,
            isMinimized = true,
            selectedWidgetId = "stepper",
            left = 0,
            top = TOP,
            width = SIZE,
            height = SIZE,
        )

        val text = dockStateText(ConsoleDockConfig(hiddenWidgetIds = setOf("inspector")), layout, widgets)

        assertEquals(
            """
            Dock on screen: left edge, bottom, minimized.
            Bounds in window pixels: left 0, top 2000, 130x130
            Dock visible in settings (section "dock", key "isVisible"): yes
            Widgets:
            - inspector "Inspector" (hidden)
            - stepper "Stepper"
            """.trimIndent(),
            text,
        )
    }

    @Test
    fun `a hidden dock points to the setting that shows it`() {
        val text = dockStateText(ConsoleDockConfig(isVisible = false), layout = null, widgetTitles = widgets)

        assertTrue(text.startsWith("No dock on screen. It is hidden; settings_set(section=\"dock\""), text)
    }

    private companion object {
        const val TOP = 2000
        const val SIZE = 130
    }
}
