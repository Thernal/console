package io.thernal.console.network.ui.common.extensions

import io.thernal.console.network.NetworkLog

internal fun NetworkLog.Request.toCurlCommand(): String {
    val parts = buildList {
        add("curl")
        add("-X")
        add(method)
        headers.forEach { (key, value) ->
            add("-H '${key.curlEscape()}: ${value.curlEscape()}'")
        }
        add("'${url.curlEscape()}'")
        body?.takeIf { it.isNotBlank() }?.let { add("--data '${it.curlEscape()}'") }
    }
    return parts.joinToString(separator = " ")
}

// Safely embed a value inside a single-quoted shell argument: a literal ' becomes '\''
// (close quote, escaped quote, reopen quote) — backslash escaping does not work inside '…'.
private fun String.curlEscape(): String {
    return replace("'", "'\\''")
}
