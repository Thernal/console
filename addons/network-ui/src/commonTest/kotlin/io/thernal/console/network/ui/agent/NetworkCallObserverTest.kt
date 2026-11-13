package io.thernal.console.network.ui.agent

import io.thernal.console.core.log.LogLevel
import io.thernal.console.network.NetworkLog
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NetworkCallObserverTest {
    @Test
    fun `a request and its response become one record under the call id`(): TestResult {
        return runTest {
            val observer = NetworkCallObserver()

            observer.emit(request(callId = "c1"))
            observer.emit(response(callId = "c1", status = OK))

            val record = observer.calls.replayCache.single()
            assertEquals("c1", record.id)
            assertEquals("GET api.example.com/v1/cart → 200 (120 ms)", record.summary)
            assertEquals("Success", record.level)
            assertEquals("200", record.fields["status"])
            assertEquals("/v1/cart", record.fields["path"])
            val detail = record.detail.orEmpty()
            assertTrue(detail.startsWith("Request\nGET https://api.example.com/v1/cart?page=2"))
            assertTrue("Response\nGET" in detail)
        }
    }

    @Test
    fun `a failed call shows its error and counts as a failure`(): TestResult {
        return runTest {
            val observer = NetworkCallObserver()

            observer.emit(request(callId = "c1"))
            observer.emit(response(callId = "c1", status = null, body = "UnknownHostException: api.example.com"))
            observer.emit(response(callId = "c2", status = OK))

            assertEquals(
                "GET api.example.com/v1/cart → failed: UnknownHostException: api.example.com",
                observer.calls.replayCache.first().summary,
            )
            val stats = observer.hosts.value.getValue("api.example.com")
            assertEquals(2, stats.calls)
            assertEquals(1, stats.failures)
            assertEquals(OK, stats.lastStatus)
        }
    }

    @Test
    fun `a large body is cut with a note`(): TestResult {
        return runTest {
            val observer = NetworkCallObserver()

            observer.emit(response(callId = "c1", status = OK, body = "x".repeat(LARGE_BODY)))

            val detail = observer.calls.replayCache.single().detail.orEmpty()
            assertTrue(detail.length < LARGE_BODY)
            assertTrue(detail.endsWith("the Network tab in the app shows all of it"))
        }
    }

    private fun request(callId: String): NetworkLog.Request {
        return NetworkLog.Request(
            method = "GET",
            url = "https://api.example.com/v1/cart?page=2",
            headers = emptyMap(),
            body = null,
            groupId = callId,
        )
    }

    private fun response(
        callId: String,
        status: Int?,
        body: String? = "{}",
    ): NetworkLog.Response {
        return NetworkLog.Response(
            method = "GET",
            url = "https://api.example.com/v1/cart?page=2",
            headers = emptyMap(),
            body = body,
            groupId = callId,
            level = if (status == null) LogLevel.Error else LogLevel.Success,
            statusCode = status,
            durationMs = if (status == null) null else DURATION,
        )
    }

    private companion object {
        const val OK = 200
        const val DURATION = 120L
        const val LARGE_BODY = 100_000
    }
}
