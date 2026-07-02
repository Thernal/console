package io.thernal.console.crash.ui.codec

import io.thernal.console.core.log.LogLevel

/**
 * Shared payload plumbing for the first-party `NetworkLog` codecs. Headers — a nested map the
 * envelope cannot hold — are flattened by the codec itself into indexed payload keys, so the
 * serialization engine only ever sees `Map<String, String>`.
 */
internal object NetworkLogPayload {

    const val KEY_METHOD = "method"
    const val KEY_URL = "url"
    const val KEY_BODY = "body"
    const val KEY_STATUS_CODE = "statusCode"
    const val KEY_DURATION_MS = "durationMs"

    private const val KEY_HEADER_COUNT = "headers.size"
    private const val KEY_HEADER_NAME_PREFIX = "headers."
    private const val KEY_HEADER_NAME_SUFFIX = ".name"
    private const val KEY_HEADER_VALUE_SUFFIX = ".value"

    fun flattenHeaders(headers: Map<String, String>): Map<String, String> {
        val flattened = LinkedHashMap<String, String>(headers.size * 2 + 1)
        flattened[KEY_HEADER_COUNT] = headers.size.toString()
        headers.entries.forEachIndexed { index, (name, value) ->
            flattened["$KEY_HEADER_NAME_PREFIX$index$KEY_HEADER_NAME_SUFFIX"] = name
            flattened["$KEY_HEADER_NAME_PREFIX$index$KEY_HEADER_VALUE_SUFFIX"] = value
        }
        return flattened
    }

    fun readHeaders(payload: Map<String, String>): Map<String, String>? {
        val count = payload[KEY_HEADER_COUNT]?.toIntOrNull() ?: return null
        val headers = LinkedHashMap<String, String>(count)
        for (index in 0 until count) {
            val name = payload["$KEY_HEADER_NAME_PREFIX$index$KEY_HEADER_NAME_SUFFIX"] ?: return null
            val value = payload["$KEY_HEADER_NAME_PREFIX$index$KEY_HEADER_VALUE_SUFFIX"] ?: return null
            headers[name] = value
        }
        return headers
    }

    fun parseLevel(
        name: String,
        default: LogLevel,
    ): LogLevel {
        return LogLevel.entries.firstOrNull { it.name == name } ?: default
    }
}
