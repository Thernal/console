package io.thernal.console.crash.ui.codec

import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.LogCodec
import io.thernal.console.crash.LogEnvelope
import io.thernal.console.network.NetworkLog
import kotlin.time.Instant

internal object NetworkResponseLogCodec : LogCodec<NetworkLog.Response> {

    /** On-disk key; must stay stable across versions or old sessions fall back to `BasicLog`. */
    const val DISCRIMINATOR: String = "network.response"

    override fun encode(log: NetworkLog.Response): Map<String, String> {
        val payload = LinkedHashMap<String, String>()
        payload[NetworkLogPayload.KEY_METHOD] = log.method
        payload[NetworkLogPayload.KEY_URL] = log.url
        log.body?.let { payload[NetworkLogPayload.KEY_BODY] = it }
        log.statusCode?.let { payload[NetworkLogPayload.KEY_STATUS_CODE] = it.toString() }
        log.durationMs?.let { payload[NetworkLogPayload.KEY_DURATION_MS] = it.toString() }
        payload.putAll(NetworkLogPayload.flattenHeaders(log.headers))
        return payload
    }

    override fun decode(
        envelope: LogEnvelope,
        payload: Map<String, String>,
    ): NetworkLog.Response? {
        val method = payload[NetworkLogPayload.KEY_METHOD] ?: return null
        val url = payload[NetworkLogPayload.KEY_URL] ?: return null
        val headers = NetworkLogPayload.readHeaders(payload) ?: return null
        return NetworkLog.Response(
            method = method,
            url = url,
            headers = headers,
            body = payload[NetworkLogPayload.KEY_BODY],
            groupId = envelope.groupId,
            id = envelope.id,
            timestamp = Instant.fromEpochMilliseconds(envelope.timestamp),
            level = NetworkLogPayload.parseLevel(name = envelope.level, default = LogLevel.Info),
            statusCode = payload[NetworkLogPayload.KEY_STATUS_CODE]?.toIntOrNull(),
            durationMs = payload[NetworkLogPayload.KEY_DURATION_MS]?.toLongOrNull(),
        )
    }
}
