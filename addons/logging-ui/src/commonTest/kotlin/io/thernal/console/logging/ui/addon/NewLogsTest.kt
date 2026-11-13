package io.thernal.console.logging.ui.addon

import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.Log
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class NewLogsTest {
    private val a = log("a")
    private val b = log("b")
    private val c = log("c")
    private val d = log("d")

    @Test
    fun `the first list passes every log and later lists only the new ones`(): TestResult {
        return runTest {
            val passed = newLogs(flowOf(listOf(a), listOf(a, b, c), listOf(b, c, d))).toList()

            assertEquals(listOf("a", "b", "c", "d"), passed.map { it.message })
        }
    }

    @Test
    fun `after a clear or an overflow the whole list is new`(): TestResult {
        return runTest {
            val cleared = newLogs(flowOf(listOf(a, b), emptyList(), listOf(c))).toList()
            val overflowed = newLogs(flowOf(listOf(a), listOf(c, d))).toList()

            assertEquals(listOf("a", "b", "c"), cleared.map { it.message })
            assertEquals(listOf("a", "c", "d"), overflowed.map { it.message })
        }
    }

    private fun log(message: String): Log {
        return BasicLog(message = message)
    }
}
