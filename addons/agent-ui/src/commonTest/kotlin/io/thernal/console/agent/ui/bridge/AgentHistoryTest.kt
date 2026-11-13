package io.thernal.console.agent.ui.bridge

import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentHistoryTest {
    @Test
    fun `append drops the oldest entries beyond the capacity`(): TestResult {
        return runTest {
            val history = AgentHistory(capacity = 3)

            for (seq in 0L until 5L) {
                history.append(AgentEntry(seq = seq, json = "$seq"))
            }

            assertEquals(listOf(2L, 3L, 4L), history.snapshot().map { it.seq })
        }
    }

    @Test
    fun `resize keeps the newest entries`(): TestResult {
        return runTest {
            val history = AgentHistory(capacity = 10)
            for (seq in 0L until 6L) {
                history.append(AgentEntry(seq = seq, json = "$seq"))
            }

            history.resize(2)

            assertEquals(listOf(4L, 5L), history.snapshot().map { it.seq })
        }
    }
}
