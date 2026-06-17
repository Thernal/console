package io.thernal.console.network.ui.common.extensions

import io.thernal.console.network.NetworkLog
import kotlin.collections.component1
import kotlin.collections.component2

internal fun NetworkLog.Request.toCurlCommand(): String {
    val headerParts = headers.entries.joinToString(separator = " ") { (key, value) ->
        "-H '${key.replace("'", "\\'")}: ${value.replace("'", "\\'")}'"
    }
    val bodyPart = body?.takeIf { it.isNotBlank() }?.let {
        " --data '${it.replace("'", "\\'")}'"
    }.orEmpty()
    return "curl -X $method $headerParts'$url'$bodyPart".replace("  ", " ").trim()
}
