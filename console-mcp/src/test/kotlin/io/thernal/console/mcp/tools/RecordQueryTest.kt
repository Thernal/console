package io.thernal.console.mcp.tools

import io.thernal.console.mcp.hub.HubRecord
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RecordQueryTest {
    private val records = listOf(
        HubRecord(
            feed = "logs",
            seq = 1,
            id = "1",
            time = 0,
            summary = "GET /users",
            level = "Debug",
            tag = "Net",
            kind = "NetworkLog",
        ),
        HubRecord(
            feed = "logs",
            seq = 2,
            id = "2",
            time = 0,
            summary = "Token expired",
            level = "Warning",
            tag = "Auth",
        ),
        HubRecord(
            feed = "logs",
            seq = 3,
            id = "3",
            time = 0,
            summary = "POST failed",
            detail = "POST /login → 500",
            level = "Error",
            tag = "net",
        ),
    )

    @Test
    fun `level is a minimum`() {
        assertEquals(listOf(2L, 3L), select(RecordQuery(minLevel = "warning")))
    }

    @Test
    fun `tag kind and text match without case and text also searches the detail`() {
        assertEquals(listOf(1L, 3L), select(RecordQuery(tag = "NET")))
        assertEquals(listOf(1L), select(RecordQuery(kind = "networklog")))
        assertEquals(listOf(3L), select(RecordQuery(text = "LOGIN")))
    }

    @Test
    fun `after skips what was already read`() {
        assertEquals(listOf(3L), select(RecordQuery(after = 2)))
    }

    @Test
    fun `a line is flat and cut at a fixed length`() {
        val long = HubRecord(
            feed = "logs",
            seq = 9,
            id = "9",
            time = 0,
            summary = "a\nb" + "x".repeat(400),
            level = "Info",
        )

        val line = long.toLine()

        assertTrue('\n' !in line)
        assertTrue(line.endsWith("… (+103 chars)"), line)
    }

    private fun select(query: RecordQuery): List<Long> {
        return records.filter(query::matches).map { it.seq }
    }
}
