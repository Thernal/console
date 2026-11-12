package io.thernal.console.agent.ui.bridge

import io.ktor.utils.io.ByteChannel
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentFramesTest {
    @Test
    fun `a written frame reads back unchanged including non ascii text`(): TestResult {
        return runTest {
            val channel = ByteChannel()
            val messages = listOf("""{"type":"log"}""", "", "Bakı — ünvan ✓")

            messages.forEach { channel.writeFrame(it) }

            assertEquals(messages, messages.map { channel.readFrame() })
        }
    }
}
