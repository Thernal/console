package io.thernal.console.inspector

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ConsoleInspectorTest {
    @AfterTest
    fun tearDown() {
        ConsoleInspector.updateConfig(InspectorConfig())
    }

    @Test
    fun `updateConfig clamps the highlight duration into its range`() {
        ConsoleInspector.updateConfig { copy(highlightDurationMillis = 1) }
        assertEquals(
            InspectorConfig.MIN_HIGHLIGHT_DURATION_MILLIS,
            ConsoleInspector.config.value.highlightDurationMillis,
        )

        ConsoleInspector.updateConfig { copy(highlightDurationMillis = Int.MAX_VALUE) }
        assertEquals(
            InspectorConfig.MAX_HIGHLIGHT_DURATION_MILLIS,
            ConsoleInspector.config.value.highlightDurationMillis,
        )
    }

    @Test
    fun `updateConfig keeps the heatmap threshold positive and auto refresh in range`() {
        ConsoleInspector.updateConfig { copy(heatmapThreshold = -5, autoRefreshSeconds = 999) }

        assertEquals(1, ConsoleInspector.config.value.heatmapThreshold)
        assertEquals(InspectorConfig.MAX_AUTO_REFRESH_SECONDS, ConsoleInspector.config.value.autoRefreshSeconds)
    }

    @Test
    fun `updateConfig with a transform applies it on top of the current config`() {
        ConsoleInspector.updateConfig { copy(showBounds = true) }
        ConsoleInspector.updateConfig { copy(showLabels = true) }

        assertEquals(true, ConsoleInspector.config.value.showBounds)
        assertEquals(true, ConsoleInspector.config.value.showLabels)
    }
}
