package io.thernal.console.inspector.ui.dock

import io.thernal.console.inspector.InspectorConfig
import io.thernal.console.inspector.InspectorFontScale
import io.thernal.console.inspector.InspectorLayoutDirection
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InspectorSummaryTest {
    @Test
    fun `a default config has no active features`() {
        assertTrue(inspectorActiveFeatures(InspectorConfig(), isFrozen = false).isEmpty())
    }

    @Test
    fun `active features are listed in card order`() {
        val config = InspectorConfig(
            showBounds = true,
            showHeatmap = true,
            fontScale = InspectorFontScale.Larger,
            layoutDirection = InspectorLayoutDirection.Rtl,
        )

        assertEquals(
            listOf("Frozen", "Bounds", "Heatmap", "Font ×1.3", "RTL"),
            inspectorActiveFeatures(config, isFrozen = true),
        )
    }

    @Test
    fun `the summary says ready when nothing is on`() {
        assertEquals("Ready", inspectorSummary(isInspecting = false, selectedName = null, activeFeatures = emptyList()))
    }

    @Test
    fun `the summary joins the active features`() {
        assertEquals(
            "Bounds · Heatmap",
            inspectorSummary(isInspecting = false, selectedName = null, activeFeatures = listOf("Bounds", "Heatmap")),
        )
    }

    @Test
    fun `while picking the summary names the pick or invites one`() {
        assertEquals("Picking · DemoCard", inspectorSummary(true, "DemoCard", listOf("Bounds")))
        assertEquals("Tap or drag to pick", inspectorSummary(true, null, emptyList()))
    }

    @Test
    fun `measuring tells what to tap next and then shows the distance`() {
        assertEquals("Measure · tap the first item", measureSummary(null, null, null))
        assertEquals("Measure · A → tap the second", measureSummary("A", null, null))
        assertEquals("A → B · ↔ 8 dp", measureSummary("A", "B", "↔ 8 dp"))
        assertEquals("Picking · A", inspectorSummary(true, "A", emptyList(), measureText = null))
        assertEquals("Measure · x", inspectorSummary(true, "A", emptyList(), measureText = "Measure · x"))
    }
}
