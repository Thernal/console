@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.crash.ui.codec

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.CrashSession
import io.thernal.console.crash.CrashSessionHeader
import io.thernal.console.crash.CrashSessionSerializer
import io.thernal.console.crash.LogCodecRegistry
import io.thernal.console.crash.LogEnvelope
import io.thernal.console.network.NetworkLog
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.time.Instant

class NetworkLogCodecTest {

    @BeforeTest
    fun registerCodecs() {
        LogCodecRegistry.register(
            type = NetworkLog.Request::class,
            discriminator = NetworkRequestLogCodec.DISCRIMINATOR,
            codec = NetworkRequestLogCodec,
        )
        LogCodecRegistry.register(
            type = NetworkLog.Response::class,
            discriminator = NetworkResponseLogCodec.DISCRIMINATOR,
            codec = NetworkResponseLogCodec,
        )
    }

    @Test
    fun `request round-trips with full fidelity`() {
        val request = NetworkLog.Request(
            method = "POST",
            url = "https://api.example.com/orders?q=a b",
            headers = linkedMapOf(
                "Content-Type" to "application/json; charset=utf-8",
                "X-Weird" to "line1\nline2",
            ),
            body = """{"id": 1,\n "note": "multi\nline"}""",
            groupId = "group-1",
            id = "req-1",
            timestamp = Instant.fromEpochMilliseconds(42),
        )

        val restored = roundTrip(request)

        val decoded = assertIs<NetworkLog.Request>(restored)
        assertEquals(request.method, decoded.method)
        assertEquals(request.url, decoded.url)
        assertEquals(request.headers, decoded.headers)
        assertEquals(request.body, decoded.body)
        assertEquals(request.groupId, decoded.groupId)
        assertEquals(request.id, decoded.id)
        assertEquals(request.timestamp, decoded.timestamp)
        assertEquals(request.level, decoded.level)
    }

    @Test
    fun `response round-trips including status and duration`() {
        val response = NetworkLog.Response(
            method = "GET",
            url = "https://api.example.com/orders/1",
            headers = mapOf("Content-Length" to "120"),
            body = null,
            groupId = "group-1",
            id = "res-1",
            timestamp = Instant.fromEpochMilliseconds(99),
            level = LogLevel.Error,
            statusCode = 503,
            durationMs = 1_250,
        )

        val restored = roundTrip(response)

        val decoded = assertIs<NetworkLog.Response>(restored)
        assertEquals(response.statusCode, decoded.statusCode)
        assertEquals(response.durationMs, decoded.durationMs)
        assertEquals(response.level, decoded.level)
        assertNull(decoded.body)
        assertEquals(response.headers, decoded.headers)
    }

    @Test
    fun `response with null status and duration keeps them null`() {
        val response = NetworkLog.Response(
            method = "GET",
            url = "https://api.example.com/ping",
            headers = emptyMap(),
            body = null,
            level = LogLevel.Warning,
            statusCode = null,
            durationMs = null,
        )

        val decoded = assertIs<NetworkLog.Response>(roundTrip(response))

        assertNull(decoded.statusCode)
        assertNull(decoded.durationMs)
    }

    @Test
    fun `a payload missing required fields decodes to null so the engine falls back`() {
        val envelope = LogEnvelope(
            type = NetworkRequestLogCodec.DISCRIMINATOR,
            id = "req-1",
            timestamp = 42,
            level = LogLevel.Info.name,
            tag = "Network",
            message = "POST orders",
            groupId = null,
        )

        val payloadMissingUrl = mapOf(NetworkLogPayload.KEY_METHOD to "POST")

        assertNull(NetworkRequestLogCodec.decode(envelope = envelope, payload = payloadMissingUrl))
        assertNull(NetworkResponseLogCodec.decode(envelope = envelope, payload = payloadMissingUrl))
    }

    private fun roundTrip(log: NetworkLog): Log {
        val session = CrashSession(
            header = CrashSessionHeader(id = "s", crashedAt = 0, summary = ""),
            logs = listOf(log),
        )
        val restored = assertNotNull(CrashSessionSerializer.deserialize(CrashSessionSerializer.serialize(session)))
        return restored.logs.single()
    }
}
