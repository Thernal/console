package io.thernal.console.network.ui.agent

import io.thernal.console.agent.AgentRecord
import io.thernal.console.network.NetworkLog

private const val MAX_DETAIL_CHARS = 64_000
private const val MAX_FAILURE_CHARS = 80
private const val FIRST_ERROR_STATUS = 400
private const val CALL_KIND = "Call"
private const val NETWORK_TAG = "Network"

/** `GET api.example.com/v1/cart → 200 (120 ms)`; a failed call shows its error instead of a status. */
internal fun networkSummary(log: NetworkLog): String {
    val call = "${log.method} ${hostOf(log.url)}${pathOf(log.url)}"
    if (log !is NetworkLog.Response) return "$call (request sent)"
    val status = log.statusCode
    val outcome = if (status == null) {
        "failed: ${log.body.orEmpty().lineSequence().firstOrNull().orEmpty().take(MAX_FAILURE_CHARS)}"
    } else {
        status.toString() + log.durationMs?.let { " ($it ms)" }.orEmpty()
    }
    return "$call → $outcome"
}

/** Structured values an agent can read without parsing the summary. */
internal fun networkFields(log: NetworkLog): Map<String, String> {
    return buildMap {
        put("method", log.method)
        put("host", hostOf(log.url))
        put("path", pathOf(log.url))
        if (log is NetworkLog.Response) {
            put("status", log.statusCode?.toString() ?: "none")
            log.durationMs?.let { put("duration_ms", it.toString()) }
        }
        log.groupId?.let { put("call_id", it) }
    }
}

/** The full text of one log, cut so a large body cannot fill a frame or the agent's context. */
internal fun networkDetail(log: NetworkLog): String {
    return capDetail(log.toShareText())
}

/** One record for a whole call: the request (when it was seen) and the response, under the call's id. */
internal fun networkCallRecord(
    request: NetworkLog.Request?,
    response: NetworkLog.Response,
): AgentRecord {
    val detail = buildString {
        if (request != null) {
            append("Request\n").append(request.toShareText()).append("\n\n")
        }
        append("Response\n").append(response.toShareText())
    }
    return AgentRecord(
        id = response.groupId ?: response.id,
        timeMillis = (request ?: response).timestamp.toEpochMilliseconds(),
        summary = networkSummary(response),
        detail = capDetail(detail),
        level = response.level.name,
        tag = NETWORK_TAG,
        kind = CALL_KIND,
        fields = networkFields(response),
    )
}

internal fun isFailure(response: NetworkLog.Response): Boolean {
    val status = response.statusCode
    return status == null || status >= FIRST_ERROR_STATUS
}

internal fun hostOf(url: String): String {
    return url.substringAfter("://").substringBefore('/').substringBefore('?')
}

/** The path without the query; the query stays in the detail, where redaction covers it. */
internal fun pathOf(url: String): String {
    val rest = url.substringAfter("://")
    if ('/' !in rest) return "/"
    return "/" + rest.substringAfter('/').substringBefore('?')
}

private fun capDetail(text: String): String {
    if (text.length <= MAX_DETAIL_CHARS) return text
    val cut = text.length - MAX_DETAIL_CHARS
    return text.take(MAX_DETAIL_CHARS) + "\n… cut: $cut more characters; the Network tab in the app shows all of it"
}
