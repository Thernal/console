package io.thernal.console.agent.ui.bridge

/** One captured log, already encoded as a protocol message; [seq] orders and de-duplicates it on the hub. */
internal data class AgentEntry(
    val seq: Long,
    val json: String,
)
