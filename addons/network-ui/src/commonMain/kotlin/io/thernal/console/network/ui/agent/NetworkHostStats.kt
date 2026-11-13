package io.thernal.console.network.ui.agent

/** Calls to one host since the app started, for `network_hosts`. A call failed when it had no status or one of 400+. */
internal data class NetworkHostStats(
    val host: String,
    val calls: Int = 0,
    val failures: Int = 0,
    val lastStatus: Int? = null,
    val totalDurationMs: Long = 0,
    val timedCalls: Int = 0,
)
