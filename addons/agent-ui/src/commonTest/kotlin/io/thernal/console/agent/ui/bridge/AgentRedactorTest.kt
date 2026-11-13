package io.thernal.console.agent.ui.bridge

import io.thernal.console.agent.AgentRecord
import kotlin.test.Test
import kotlin.test.assertEquals

class AgentRedactorTest {
    private val redactor = AgentRedactor(AgentRedactor.DEFAULT_KEYS + "pin")

    @Test
    fun `json members are masked whatever their value`() {
        val text = """{"accessToken": "abc\"def", "user": "ali", "pinCode": 1234, "nested": {"password":"p"}}"""

        assertEquals(
            """{"accessToken": "[redacted]", "user": "ali", "pinCode": "[redacted]", "nested": {"password":"[redacted]"}}""",
            redactor.redact(text),
        )
    }

    @Test
    fun `query parameters and header lines are masked`() {
        val text = "GET /login?user=ali&token=abc123 HTTP/1.1\nAuthorization: Bearer xyz\nAccept: */*"

        assertEquals(
            "GET /login?user=ali&token=[redacted] HTTP/1.1\nAuthorization: [redacted]\nAccept: */*",
            redactor.redact(text),
        )
    }

    @Test
    fun `record fields with a sensitive key are masked`() {
        val record = AgentRecord(
            id = "1",
            timeMillis = 0,
            summary = "Login",
            fields = mapOf("sessionToken" to "abc", "status" to "200"),
        )

        assertEquals(mapOf("sessionToken" to "[redacted]", "status" to "200"), redactor.redact(record).fields)
    }
}
