package io.thernal.console.inspector.ui.engine

import androidx.compose.runtime.tooling.CompositionData
import androidx.compose.runtime.tooling.CompositionGroup
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

class InspectionTablesTest {

    private fun compositionData(): CompositionData {
        return object : CompositionData {
            override val compositionGroups: Iterable<CompositionGroup> = emptyList()
            override val isEmpty: Boolean = true
        }
    }

    @Test
    fun `stays a single element of a hash set while compositions are added to it`() {
        val tables = InspectionTables()
        val owner = HashSet<Set<CompositionData>>()

        repeat(times = 64) {
            owner.add(tables)
            tables.add(compositionData())
        }

        assertEquals(1, owner.size)
        assertEquals(64, tables.size)
    }

    @Test
    fun `is equal only to itself`() {
        val tables = InspectionTables()

        assertEquals(tables, tables)
        assertNotEquals<Set<CompositionData>>(InspectionTables(), tables)
        assertFalse(tables.equals(emptySet<CompositionData>()))
    }
}
