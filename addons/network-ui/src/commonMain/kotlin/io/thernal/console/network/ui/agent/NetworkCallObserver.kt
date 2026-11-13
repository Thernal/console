package io.thernal.console.network.ui.agent

import io.thernal.console.agent.AgentRecord
import io.thernal.console.core.LogObserver
import io.thernal.console.core.log.Log
import io.thernal.console.network.NetworkLog
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Pairs each request with its response (they share a `groupId`) into one record per call for the `network` feed, and
 * counts calls per host. A call is recorded when its response or failure arrives; a request still waiting shows in
 * the `logs` feed. [emit] never suspends: the pipeline awaits observers one by one.
 */
internal class NetworkCallObserver : LogObserver {
    /** Requests waiting for their response, by call id. Only touched by [emit], which the pipeline calls serially. */
    private val pending = LinkedHashMap<String, NetworkLog.Request>()

    private val _calls = MutableSharedFlow<AgentRecord>(
        replay = REPLAY_CALLS,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val calls: SharedFlow<AgentRecord> = _calls.asSharedFlow()

    private val _hosts = MutableStateFlow<Map<String, NetworkHostStats>>(emptyMap())
    val hosts: StateFlow<Map<String, NetworkHostStats>> = _hosts.asStateFlow()

    override suspend fun emit(event: Log) {
        when (event) {
            is NetworkLog.Request -> remember(event)
            is NetworkLog.Response -> record(event)
            else -> Unit
        }
    }

    private fun remember(request: NetworkLog.Request) {
        val callId = request.groupId ?: return
        pending[callId] = request
        if (pending.size > MAX_PENDING) {
            pending.remove(pending.keys.first())
        }
    }

    private fun record(response: NetworkLog.Response) {
        val request = response.groupId?.let(pending::remove)
        _calls.tryEmit(networkCallRecord(request = request, response = response))
        val host = hostOf(response.url)
        _hosts.update { hosts ->
            val stats = hosts[host] ?: NetworkHostStats(host = host)
            hosts + (host to stats.counting(response))
        }
    }

    private fun NetworkHostStats.counting(response: NetworkLog.Response): NetworkHostStats {
        val duration = response.durationMs
        return copy(
            calls = calls + 1,
            failures = failures + if (isFailure(response)) 1 else 0,
            lastStatus = response.statusCode,
            totalDurationMs = totalDurationMs + (duration ?: 0),
            timedCalls = timedCalls + if (duration != null) 1 else 0,
        )
    }

    private companion object {
        const val REPLAY_CALLS = 500
        const val MAX_PENDING = 500
    }
}
