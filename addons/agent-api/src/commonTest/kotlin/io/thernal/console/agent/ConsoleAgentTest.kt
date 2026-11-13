package io.thernal.console.agent

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ConsoleAgentTest {
    @AfterTest
    fun tearDown() {
        ConsoleAgent.updateConfig(AgentConfig())
    }

    @Test
    fun `updateConfig keeps the port in the valid range`() {
        ConsoleAgent.updateConfig { copy(hubPort = 0) }
        assertEquals(AgentConfig.MIN_PORT, ConsoleAgent.config.value.hubPort)

        ConsoleAgent.updateConfig { copy(hubPort = Int.MAX_VALUE) }
        assertEquals(AgentConfig.MAX_PORT, ConsoleAgent.config.value.hubPort)
    }

    @Test
    fun `updateConfig clamps the history size`() {
        ConsoleAgent.updateConfig { copy(historySize = 1) }
        assertEquals(AgentConfig.MIN_HISTORY_SIZE, ConsoleAgent.config.value.historySize)

        ConsoleAgent.updateConfig { copy(historySize = Int.MAX_VALUE) }
        assertEquals(AgentConfig.MAX_HISTORY_SIZE, ConsoleAgent.config.value.historySize)
    }

    @Test
    fun `updateConfig falls back to the default host and drops a blank device name`() {
        ConsoleAgent.updateConfig(AgentConfig(hubHost = "  ", deviceName = " "))

        assertEquals(AgentConfig.DEFAULT_HUB_HOST, ConsoleAgent.config.value.hubHost)
        assertNull(ConsoleAgent.config.value.deviceName)
    }
}
