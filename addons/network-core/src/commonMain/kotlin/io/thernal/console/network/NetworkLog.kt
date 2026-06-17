package io.thernal.console.network

import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface NetworkLog : Log {
    val method: String
    val url: String
    val headers: Map<String, String>
    val body: String?

    override val tag: String get() = "Network"

    override fun contains(query: String): Boolean {
        if (query.isBlank()) {
            return false
        }

        return buildString {
            append(method)
            append(url)
            for (pair in headers) {
                append(pair.key)
                append(pair.value)
            }
            body?.let(::append)

            if (this@NetworkLog is Response) {
                statusCode?.toString()?.let(::append)
                durationMs?.toString()?.let(::append)
                append(message)
            }
        }.contains(query, ignoreCase = true)
    }

    override fun toShareText(): String {
        return buildString {
            appendLine("$method $url")
            if (this@NetworkLog is Response) {
                statusCode?.let { appendLine("Status: $it") }
                durationMs?.let { appendLine("Duration: ${it}ms") }
            }

            appendLine()
            appendLine("Headers:")
            append(headers.entries.joinToString(separator = "\n") { "${it.key}: ${it.value}" })

            body?.takeIf { it.isNotBlank() }?.let { rawBody ->
                appendLine()
                appendLine()
                appendLine("Body:")
                append(resolveNetworkBody(rawBody = rawBody, headers = headers).toCopyText())
            }
        }.trim()
    }

    @OptIn(ExperimentalUuidApi::class)
    data class Request(
        override val method: String,
        override val url: String,
        override val headers: Map<String, String>,
        override val body: String?,
        override val groupId: String? = Uuid.random().toString(),
        override val id: String = Uuid.random().toString(),
        override val timestamp: Instant = Clock.System.now(),
        override val level: LogLevel = LogLevel.Info,
        override val tab: String = "Request",
    ) : NetworkLog {
        override val message: String = buildString {
            append("$method ")
            append(url.substringAfter("://").substringAfter('/'))
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    data class Response(
        override val method: String,
        override val url: String,
        override val headers: Map<String, String>,
        override val body: String?,
        override val groupId: String? = Uuid.random().toString(),
        override val id: String = Uuid.random().toString(),
        override val timestamp: Instant = Clock.System.now(),
        override val level: LogLevel,
        override val tab: String = "Response",
        val statusCode: Int?,
        val durationMs: Long?,
    ) : NetworkLog {

        override val message: String = buildString {
            append("$method ")
            append(url.substringAfter("://").substringAfter('/'))
            append(" → ")
            append(statusCode ?: "UNKNOWN")
            append(durationMs?.let { " (${it}ms)" }.orEmpty())
        }
    }
}
