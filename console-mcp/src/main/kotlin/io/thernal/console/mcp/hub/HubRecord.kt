package io.thernal.console.mcp.hub

import kotlinx.serialization.Serializable

/** One feed entry as the app's agent bridge sends it (`type: record`). */
@Serializable
data class HubRecord(
    val feed: String,
    val seq: Long,
    val id: String,
    val time: Long,
    val summary: String,
    val detail: String? = null,
    val level: String? = null,
    val tag: String? = null,
    val kind: String? = null,
    val fields: Map<String, String> = emptyMap(),
)
