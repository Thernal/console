package io.thernal.console.mcp.hub

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class HubProtocolTest {
    @Test
    fun `the hub reads the protocol it speaks`() {
        assertNull(HubProtocol.problem(HubProtocol.CURRENT))
    }

    @Test
    fun `a newer app asks for a newer hub`() {
        val problem = assertNotNull(HubProtocol.problem(HubProtocol.CURRENT + 1))

        assertTrue(problem.contains("install the console-mcp"))
    }

    @Test
    fun `an older app asks for a newer console in the app`() {
        val problem = assertNotNull(HubProtocol.problem(HubProtocol.OLDEST - 1))

        assertTrue(problem.contains("update Console in the app"))
    }

    @Test
    fun `an app that names no protocol is not read`() {
        assertNotNull(HubProtocol.problem(null))
    }
}
