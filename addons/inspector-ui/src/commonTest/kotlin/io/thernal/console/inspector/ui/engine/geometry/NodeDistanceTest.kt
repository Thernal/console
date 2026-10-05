package io.thernal.console.inspector.ui.engine.geometry

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NodeDistanceTest {
    @Test
    fun `items side by side have only a horizontal gap at the middle of their overlap`() {
        val distance = measureDistance(Rect(0f, 0f, 100f, 100f), Rect(130f, 20f, 200f, 60f))

        assertEquals(30f, distance.horizontal)
        assertEquals(0f, distance.vertical)
        assertEquals(listOf(DistanceSegment(Offset(100f, 40f), Offset(130f, 40f))), distance.segments)
    }

    @Test
    fun `items stacked have only a vertical gap`() {
        val distance = measureDistance(Rect(0f, 0f, 100f, 50f), Rect(0f, 80f, 100f, 120f))

        assertEquals(0f, distance.horizontal)
        assertEquals(30f, distance.vertical)
        assertEquals(listOf(DistanceSegment(Offset(50f, 50f), Offset(50f, 80f))), distance.segments)
    }

    @Test
    fun `the order of the two items does not change the gaps`() {
        val a = Rect(0f, 0f, 100f, 50f)
        val b = Rect(160f, 90f, 200f, 120f)

        val forward = measureDistance(a, b)
        val backward = measureDistance(b, a)

        assertEquals(forward.horizontal, backward.horizontal)
        assertEquals(forward.vertical, backward.vertical)
        assertEquals(60f, forward.horizontal)
        assertEquals(40f, forward.vertical)
    }

    @Test
    fun `diagonal items get an L shaped pair of segments`() {
        val distance = measureDistance(Rect(0f, 0f, 100f, 50f), Rect(160f, 90f, 200f, 120f))

        assertEquals(
            listOf(
                DistanceSegment(Offset(100f, 50f), Offset(160f, 50f)),
                DistanceSegment(Offset(160f, 50f), Offset(160f, 90f)),
            ),
            distance.segments,
        )
    }

    @Test
    fun `a nested item reports its four insets`() {
        val distance = measureDistance(Rect(0f, 0f, 200f, 100f), Rect(20f, 10f, 180f, 70f))

        assertTrue(distance.isNested)
        assertEquals(listOf(20f, 20f, 10f, 30f), distance.segments.map { it.length })
    }

    @Test
    fun `partly overlapping items have no distance`() {
        val distance = measureDistance(Rect(0f, 0f, 100f, 100f), Rect(50f, 50f, 150f, 150f))

        assertTrue(distance.segments.isEmpty())
        assertEquals("Overlapping", distance.describe(density = 1f))
    }

    @Test
    fun `items that share an edge are touching, not overlapping`() {
        val distance = measureDistance(Rect(0f, 0f, 100f, 40f), Rect(0f, 40f, 100f, 90f))

        assertTrue(distance.segments.isEmpty())
        assertEquals("Touching · 0 dp", distance.describe(density = 2f))
    }

    @Test
    fun `descriptions are in dp`() {
        val gaps = measureDistance(Rect(0f, 0f, 100f, 50f), Rect(160f, 90f, 200f, 120f))

        assertEquals("↔ 30 · ↕ 20 dp", gaps.describe(density = 2f))
    }
}
