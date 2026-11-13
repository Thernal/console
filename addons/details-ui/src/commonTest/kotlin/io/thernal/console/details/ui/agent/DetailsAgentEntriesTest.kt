package io.thernal.console.details.ui.agent

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DetailsAgentEntriesTest {
    @Test
    fun `details are listed and filtered by key or value`() {
        val details = linkedMapOf("user_id" to "42", "plan" to "pro", "build" to "1.4.0")

        assertEquals("user_id = 42\nplan = pro\nbuild = 1.4.0", detailsText(details, query = null))
        assertEquals("plan = pro", detailsText(details, query = "PRO"))
        assertEquals("No detail matches \"x\"; 3 details in all.", detailsText(details, query = "x"))
    }

    @Test
    fun `each set and change and removal is one record`(): TestResult {
        return runTest {
            val states = flowOf(
                mapOf("user_id" to "41"),
                mapOf("user_id" to "42", "promo" to "spring"),
                mapOf("user_id" to "42"),
            )

            val records = detailChanges(states).toList()

            assertEquals(
                listOf(
                    "user_id: (none) → 41",
                    "user_id: 41 → 42",
                    "promo: (none) → spring",
                    "promo removed (was spring)",
                ),
                records.map { it.summary },
            )
            assertEquals(listOf("detail-0", "detail-1", "detail-2", "detail-3"), records.map { it.id })
        }
    }
}
