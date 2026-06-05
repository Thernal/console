@file:OptIn(ExperimentalUuidApi::class)

package io.thernal.console.network.ktor

import io.ktor.client.call.save
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.content.TextContent
import io.thernal.console.network.NetworkLog
import io.thernal.console.runtime.console.Console
import io.thernal.console.runtime.log.LogLevel
import kotlin.time.TimeSource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val SensitiveHeaderNames = setOf(
    "authorization",
    "cookie",
    "set-cookie",
    "x-api-key",
    "proxy-authorization",
)

val ConsoleNetworkKtorPlugin = createClientPlugin("ConsoleNetwork") {
    on(Send) { request ->
        val startedAt = TimeSource.Monotonic.markNow()
        val method = request.method.value
        val url = request.url.buildString()
        val requestHeaders = request.headers.build().toHeaderMap()
        val requestBody = request.body.toLogBody()
        val groupId = Uuid.random().toString()

        Console.asyncNotify {
            NetworkLog.Request(
                method = method,
                url = url,
                headers = requestHeaders,
                body = requestBody,
                groupId = groupId,
            )
        }

        try {
            val call = proceed(request)
            val elapsedMs = startedAt.elapsedNow().inWholeMilliseconds
            val savedCall = runCatching { call.save() }.getOrNull()
            val response = savedCall?.response ?: call.response
            val statusCode = response.status.value
            val responseHeaders = response.headers.toHeaderMap()
            val responseBody = runCatching { response.bodyAsText() }
                .getOrElse { error -> error.toUnavailableBody() }

            Console.asyncNotify {
                NetworkLog.Response(
                    method = method,
                    url = url,
                    statusCode = statusCode,
                    level = statusCode.toNetworkLevel(),
                    headers = responseHeaders,
                    body = responseBody,
                    durationMs = elapsedMs,
                    groupId = groupId,
                )
            }

            call
        } catch (error: ResponseException) {
            val elapsedMs = startedAt.elapsedNow().inWholeMilliseconds
            val statusCode = error.response.status.value
            val responseHeaders = error.response.headers.toHeaderMap()
            val responseBody = runCatching { error.response.call.save().response.bodyAsText() }
                .getOrElse { bodyError -> bodyError.toUnavailableBody() }

            Console.asyncNotify {
                NetworkLog.Response(
                    method = method,
                    url = url,
                    statusCode = statusCode,
                    level = statusCode.toNetworkLevel(),
                    headers = responseHeaders,
                    body = responseBody,
                    durationMs = elapsedMs,
                    groupId = groupId,
                )
            }

            throw error
        } catch (error: Throwable) {
            val elapsedMs = startedAt.elapsedNow().inWholeMilliseconds

            Console.asyncNotify {
                NetworkLog.Response(
                    method = method,
                    url = url,
                    statusCode = null,
                    level = LogLevel.Error,
                    headers = emptyMap(),
                    body = "${error::class.simpleName}: ${error.message.orEmpty()}",
                    durationMs = elapsedMs,
                    groupId = groupId,
                )
            }

            throw error
        }
    }
}

private fun Headers.toHeaderMap(): Map<String, String> {
    return entries().associate { (key, values) ->
        key to if (key.lowercase() in SensitiveHeaderNames) {
            "***"
        } else {
            values.joinToString(separator = ", ")
        }
    }
}

private fun Any.toLogBody(): String? {
    return when (this) {
        is TextContent -> text
        is OutgoingContent.NoContent -> null
        is OutgoingContent.ByteArrayContent -> "<binary: ${bytes().size} bytes>"
        is OutgoingContent.ReadChannelContent -> "<stream: ReadChannelContent>"
        is OutgoingContent.WriteChannelContent -> "<stream: WriteChannelContent>"
        is OutgoingContent -> toString()
        else -> toString()
    }
}

private fun Throwable.toUnavailableBody(): String {
    return "<unavailable: ${this::class.simpleName}: ${message.orEmpty()}>"
}

private fun Int.toNetworkLevel(): LogLevel {
    return when {
        this < HttpStatusCode.MultipleChoices.value -> LogLevel.Success
        this < HttpStatusCode.BadRequest.value -> LogLevel.Info
        this < HttpStatusCode.InternalServerError.value -> LogLevel.Warning
        else -> LogLevel.Error
    }
}
