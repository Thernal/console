package io.thernal.console.crash.ui.codec

import io.thernal.console.core.log.LogLevel
import io.thernal.console.crash.LogCodec
import io.thernal.console.crash.LogEnvelope
import io.thernal.console.network.NetworkLog
import kotlin.time.Instant

internal object NetworkRequestLogCodec : LogCodec<NetworkLog.Request> {

    /** On-disk key; must stay stable across versions or old sessions fall back to `BasicLog`. */
    const val DISCRIMINATOR: String = "network.request"

    override fun encode(log: NetworkLog.Request): Map<String, String> {
        val payload = LinkedHashMap<String, String>()
        payload[NetworkLogPayload.KEY_METHOD] = log.method
        payload[NetworkLogPayload.KEY_URL] = log.url
        log.body?.let { payload[NetworkLogPayload.KEY_BODY] = it }
        payload.putAll(NetworkLogPayload.flattenHeaders(log.headers))
        return payload
    }

    override fun decode(
        envelope: LogEnvelope,
        payload: Map<String, String>,
    ): NetworkLog.Request? {
        val method = payload[NetworkLogPayload.KEY_METHOD] ?: return null
        val url = payload[NetworkLogPayload.KEY_URL] ?: return null
        val headers = NetworkLogPayload.readHeaders(payload) ?: return null
        return NetworkLog.Request(
            method = method,
            url = url,
            headers = headers,
            body = payload[NetworkLogPayload.KEY_BODY],
            groupId = envelope.groupId,
            id = envelope.id,
            timestamp = Instant.fromEpochMilliseconds(envelope.timestamp),
            level = NetworkLogPayload.parseLevel(name = envelope.level, default = LogLevel.Info),
        )
    }
}
