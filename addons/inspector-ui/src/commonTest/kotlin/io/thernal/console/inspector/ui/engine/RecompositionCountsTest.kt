package io.thernal.console.inspector.ui.engine

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class RecompositionCountsTest {
    @Test
    fun `the first pass of a scope is its initial composition and is not counted`() {
        val counts = RecompositionCounts()

        assertTrue(counts.isFirstSight("scope"))
        assertFalse(counts.isFirstSight("scope"))
    }

    @Test
    fun `scopes the tree already contained are not first sights`() {
        val counts = RecompositionCounts()
        counts.markSeen(listOf("known"))

        assertFalse(counts.isFirstSight("known"))
        assertTrue(counts.isFirstSight("new"))
    }

    @Test
    fun `reset forgets counts but keeps what has been seen`() {
        val counts = RecompositionCounts()
        counts.isFirstSight("scope")
        counts.increment("scope")

        counts.reset()

        assertFalse(counts.isFirstSight("scope"))
        assertEquals(emptyMap(), counts.byNode(InspectorSnapshot.Empty, hideFramework = false))
    }

    @Test
    fun `a node that is credited nothing yields no counts for an empty snapshot`() {
        val counts = RecompositionCounts()
        counts.isFirstSight("scope")
        counts.increment("scope")

        assertEquals(emptyMap(), counts.byNode(InspectorSnapshot.Empty, hideFramework = true))
    }

    @Test
    fun `skipped passes are kept apart from recompositions that ran`() {
        val counts = RecompositionCounts()
        counts.isFirstSight("scope")
        counts.increment("scope")
        counts.skip("scope")
        counts.skip("scope")

        assertEquals(emptyMap(), counts.byNode(InspectorSnapshot.Empty, hideFramework = false, skipped = true))
        counts.reset()
        counts.skip("scope")
        assertEquals(emptyMap(), counts.byNode(InspectorSnapshot.Empty, hideFramework = false))
    }
}
