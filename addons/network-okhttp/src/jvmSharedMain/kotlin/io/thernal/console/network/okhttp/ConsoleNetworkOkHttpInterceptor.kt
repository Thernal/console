package io.thernal.console.network.okhttp

import io.thernal.console.network.NetworkLog
import io.thernal.console.network.SensitiveHeaders
import io.thernal.console.network.toNetworkLevel
import io.thernal.console.runtime.console.Console
import io.thernal.console.runtime.log.LogLevel
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ConsoleNetworkOkHttpInterceptor(
    private val maxBodyBytes: Long = MAX_BODY_BYTES,
    private val sensitiveHeaders: SensitiveHeaders = SensitiveHeaders.DEFAULT,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val groupId = Uuid.random().toString()
        val method = request.method
        val url = request.url.toString()
        val requestHeaders = request.headers.toLogMap()
        val requestBody = request.body?.readToString()

        Console.notify {
            NetworkLog.Request(
                method = method,
                url = url,
                headers = requestHeaders,
                body = requestBody,
                groupId = groupId,
            )
        }

        val startMs = System.currentTimeMillis()

        return try {
            val response = chain.proceed(request)
            val durationMs = System.currentTimeMillis() - startMs
            val responseBody = runCatching { response.peekBody(maxBodyBytes).string() }.getOrNull()
            val responseHeaders = response.headers.toLogMap()

            Console.notify {
                NetworkLog.Response(
                    method = method,
                    url = url,
                    statusCode = response.code,
                    level = response.code.toNetworkLevel(),
                    headers = responseHeaders,
                    body = responseBody,
                    durationMs = durationMs,
                    groupId = groupId,
                )
            }

            response
        } catch (e: Exception) {
            val durationMs = System.currentTimeMillis() - startMs

            Console.notify {
                NetworkLog.Response(
                    method = method,
                    url = url,
                    statusCode = null,
                    level = LogLevel.Error,
                    headers = emptyMap(),
                    body = "${e::class.simpleName}: ${e.message.orEmpty()}",
                    durationMs = durationMs,
                    groupId = groupId,
                )
            }

            throw e
        }
    }

    private fun RequestBody.readToString(): String? {
        return runCatching {
            val buffer = Buffer()
            writeTo(buffer)
            buffer.readUtf8()
        }.getOrNull()
    }

    private fun Headers.toLogMap(): Map<String, String> = names().associateWith { name ->
        if (sensitiveHeaders.shouldMask(name)) {
            sensitiveHeaders.mask
        } else {
            values(name).joinToString(", ")
        }
    }

    companion object {
        private const val MAX_BODY_BYTES = 64 * 1024L
    }
}
