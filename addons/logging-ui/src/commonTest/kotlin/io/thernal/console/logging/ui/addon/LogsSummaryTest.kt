package io.thernal.console.logging.ui.addon

import io.thernal.console.core.log.BasicLog
import io.thernal.console.core.log.LogLevel
import io.thernal.console.logging.ui.ConsoleLogObserver
import kotlin.test.Test
import kotlin.test.assertEquals

class LogsSummaryTest {
    @Test
    fun `levels and kinds and tags are counted with the highest level per tag`() {
        val logs = listOf(
            BasicLog(message = "a", tag = "Net", level = LogLevel.Info),
            BasicLog(message = "b", tag = "Net", level = LogLevel.Error),
            BasicLog(message = "c"),
            BasicLog(message = "step 1", tag = AGENT_LOG_TAG, level = LogLevel.Info),
        )

        val summary = logsSummary(logs = logs, config = ConsoleLogObserver.Config(maxLogCount = MAX_LOGS))

        val expected = """
            Logging on: 4 logs kept in the app, up to 50.
            Levels: Error 1, Info 2, None 1
            Kinds: BasicLog 4
            Tags:
            Net 2, highest Error
            (no tag) 1
            agent 1, highest Info
        """.trimIndent()
        assertEquals(expected, summary)
    }

    @Test
    fun `logging off is explained even with nothing kept`() {
        val summary = logsSummary(logs = emptyList(), config = ConsoleLogObserver.Config(enabled = false))

        val lines = summary.lines()
        assertEquals("Logging off: 0 logs kept in the app, up to 1000.", lines.first())
        assertEquals(EXPECTED_OFF_LINES, lines.size)
    }

    @Test
    fun `tags past the cap are counted and not listed`() {
        val logs = List(TAG_COUNT) { BasicLog(message = "m", tag = "tag$it") }

        val lines = logsSummary(logs = logs, config = ConsoleLogObserver.Config()).lines()

        assertEquals("… 5 more tags", lines.last())
        assertEquals("tag29 1", lines[lines.lastIndex - 1])
    }

    private companion object {
        const val MAX_LOGS = 50
        const val EXPECTED_OFF_LINES = 2
        const val TAG_COUNT = 35
    }
}
