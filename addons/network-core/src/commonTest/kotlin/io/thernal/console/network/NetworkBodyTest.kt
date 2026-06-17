package io.thernal.console.network

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NetworkBodyTest {

    @Test
    fun `json content type is pretty printed`() {
        val body = resolveNetworkBody(
            rawBody = """{"id":1}""",
            headers = mapOf("Content-Type" to "application/json"),
        )

        val text = assertIs<NetworkBody.Text>(body)
        assertEquals("{\n  \"id\": 1\n}", text.value)
    }

    @Test
    fun `content type with charset is still detected as json`() {
        val body = resolveNetworkBody(
            rawBody = """{"id":1}""",
            headers = mapOf("Content-Type" to "application/json; charset=utf-8"),
        )

        assertIs<NetworkBody.Text>(body)
    }

    @Test
    fun `header lookup is case insensitive`() {
        val body = resolveNetworkBody(
            rawBody = """{"id":1}""",
            headers = mapOf("content-type" to "application/json"),
        )

        assertIs<NetworkBody.Text>(body)
    }

    @Test
    fun `invalid json falls back to raw text`() {
        val raw = """{"id":}"""
        val body = resolveNetworkBody(
            rawBody = raw,
            headers = mapOf("Content-Type" to "application/json"),
        )

        val text = assertIs<NetworkBody.Text>(body)
        assertEquals(raw, text.value)
    }

    @Test
    fun `unsupported text content stays raw`() {
        val raw = "hello world"
        val body = resolveNetworkBody(
            rawBody = raw,
            headers = mapOf("Content-Type" to "text/plain"),
        )

        val text = assertIs<NetworkBody.Text>(body)
        assertEquals(raw, text.value)
    }

    @Test
    fun `json without content type is detected by shape`() {
        val body = resolveNetworkBody(rawBody = """{"id":1}""", headers = emptyMap())

        assertIs<NetworkBody.Text>(body)
    }

    @Test
    fun `binary content type yields metadata`() {
        val body = resolveNetworkBody(
            rawBody = "ignored",
            headers = mapOf("Content-Type" to "image/png", "Content-Length" to "2048"),
        )

        val binary = assertIs<NetworkBody.Binary>(body)
        assertEquals("image/png", binary.mimeType)
        assertEquals(2048L, binary.byteCount)
    }

    @Test
    fun `octet stream is treated as binary`() {
        val body = resolveNetworkBody(
            rawBody = "anything",
            headers = mapOf("Content-Type" to "application/octet-stream"),
        )

        assertIs<NetworkBody.Binary>(body)
    }

    @Test
    fun `unreadable bytes are detected as binary without content type`() {
        val body = resolveNetworkBody(rawBody = "PNG\u0000\uFFFD data", headers = emptyMap())

        assertIs<NetworkBody.Binary>(body)
    }

    @Test
    fun `binary copy text lists metadata`() {
        val body = NetworkBody.Binary(mimeType = "image/png", byteCount = 2048L)

        assertEquals(
            "Content-Type: image/png\nContent-Length: 2048 bytes (2 KB)",
            body.toCopyText(),
        )
    }

    @Test
    fun `binary copy text omits length when unknown`() {
        val body = NetworkBody.Binary(mimeType = null, byteCount = null)

        assertEquals("Content-Type: unknown", body.toCopyText())
    }

    @Test
    fun `human readable size scales by unit`() {
        assertEquals("0 B", 0L.toHumanReadableSize())
        assertEquals("512 B", 512L.toHumanReadableSize())
        assertEquals("1 KB", 1024L.toHumanReadableSize())
        assertEquals("1.5 KB", 1536L.toHumanReadableSize())
        assertEquals("1 MB", (1024L * 1024).toHumanReadableSize())
        assertEquals("1 GB", (1024L * 1024 * 1024).toHumanReadableSize())
    }

    @Test
    fun `text copy text returns the value verbatim`() {
        assertTrue(NetworkBody.Text("raw").toCopyText() == "raw")
    }
}
