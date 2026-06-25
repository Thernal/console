package io.thernal.console.api.addon

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Behavioural tests for [ConsoleNavigation] registration: deduplication and tab ordering.
 *
 * [ConsoleNavigation] is a process-wide `object` whose `_tabs`/`_graphs` persist across tests, so
 * every assertion is written as a delta from a captured baseline and verifies only the entries it
 * adds (by identity) — never absolute sizes or positions.
 */
class ConsoleNavigationTest {

    /** Two instances of one class stand in for what R8 class-merging produces from two `object`s. */
    private class FakeGraph : ConsoleNavGraph {
        override fun EntryProviderScope<NavKey>.routes() = Unit
    }

    private class FakeTab(override val order: Int) : ConsoleTab {
        override val title: String = "fake-$order"
        override val icon: ImageVector =
            ImageVector.Builder("fake", 1.dp, 1.dp, 1f, 1f).build()

        @Composable
        override fun Content() = Unit
    }

    // --- graphs ---------------------------------------------------------------------------------

    /**
     * Regression: #18 ("Unknown screen" crash in minified builds). R8 horizontal class merging can
     * fold two graph `object`s into one class; a `::class`-based dedup then drops the second as a
     * "duplicate" and its routes never register. Identity dedup keeps both, since merging never
     * collapses two distinct instances into one reference.
     */
    @Test
    fun registerGraph_distinctInstancesOfSameClass_allRegister() {
        val base = ConsoleNavigation.graphs.size
        val first = FakeGraph()
        val second = FakeGraph() // first::class == second::class, first !== second

        ConsoleNavigation.registerGraph(first)
        ConsoleNavigation.registerGraph(second)

        assertEquals(base + 2, ConsoleNavigation.graphs.size, "second graph was dropped by class-based dedup")
        assertTrue(first in ConsoleNavigation.graphs)
        assertTrue(second in ConsoleNavigation.graphs)
    }

    @Test
    fun registerGraph_sameInstanceTwice_registersOnce() {
        val base = ConsoleNavigation.graphs.size
        val graph = FakeGraph()

        ConsoleNavigation.registerGraph(graph)
        ConsoleNavigation.registerGraph(graph)

        assertEquals(base + 1, ConsoleNavigation.graphs.size, "identity dedup must still guard true duplicates")
    }

    // --- tabs -----------------------------------------------------------------------------------

    /** Same R8-merge hazard as graphs: distinct tab instances of one class must all register. */
    @Test
    fun registerTab_distinctInstancesOfSameClass_allRegister() {
        val base = ConsoleNavigation.tabs.size
        val first = FakeTab(order = 100)
        val second = FakeTab(order = 101)

        ConsoleNavigation.registerTab(first)
        ConsoleNavigation.registerTab(second)

        assertEquals(base + 2, ConsoleNavigation.tabs.size, "second tab was dropped by class-based dedup")
        assertTrue(first in ConsoleNavigation.tabs)
        assertTrue(second in ConsoleNavigation.tabs)
    }

    @Test
    fun registerTab_sameInstanceTwice_registersOnce() {
        val base = ConsoleNavigation.tabs.size
        val tab = FakeTab(order = 200)

        ConsoleNavigation.registerTab(tab)
        ConsoleNavigation.registerTab(tab)

        assertEquals(base + 1, ConsoleNavigation.tabs.size, "identity dedup must still guard true duplicates")
    }

    /** Tabs are inserted by ascending [ConsoleTab.order], regardless of registration order. */
    @Test
    fun registerTab_insertsInAscendingOrder() {
        // Distinct order band unlikely to interleave with real tabs, registered out of order.
        val mid = FakeTab(order = 9_002)
        val low = FakeTab(order = 9_001)
        val high = FakeTab(order = 9_003)

        ConsoleNavigation.registerTab(mid)
        ConsoleNavigation.registerTab(low)
        ConsoleNavigation.registerTab(high)

        val mine = ConsoleNavigation.tabs.filter { it === low || it === mid || it === high }
        assertEquals(listOf(9_001, 9_002, 9_003), mine.map { it.order }, "tabs not ordered by `order`")
    }
}
