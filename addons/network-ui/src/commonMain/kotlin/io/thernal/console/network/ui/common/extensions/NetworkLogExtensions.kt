package io.thernal.console.network.ui.common.extensions

import io.thernal.console.network.NetworkLog
import kotlin.collections.component1
import kotlin.collections.component2

internal fun NetworkLog.toShareText(): String {
    return buildString {
        appendLine("$method $url")
        if (this@toShareText is NetworkLog.Response) {
            statusCode?.let { appendLine("Status: $it") }
            durationMs?.let { appendLine("Duration: ${it}ms") }
        }
        appendLine()
        appendLine("Headers:")
        appendLine(headers.toDisplayText())
        body?.takeIf { it.isNotBlank() }?.let {
            appendLine()
            appendLine("Body:")
            append(resolveNetworkBody(rawBody = it, headers = headers).toCopyText())
        }
    }.trim()
}

internal fun NetworkLog.Request.toCurlCommand(): String {
    val headerParts = headers.entries.joinToString(separator = " ") { (key, value) ->
        "-H '${key.replace("'", "\\'")}: ${value.replace("'", "\\'")}'"
    }
    val bodyPart = body?.takeIf { it.isNotBlank() }?.let {
        " --data '${it.replace("'", "\\'")}'"
    }.orEmpty()
    return "curl -X $method $headerParts'$url'$bodyPart".replace("  ", " ").trim()
}
