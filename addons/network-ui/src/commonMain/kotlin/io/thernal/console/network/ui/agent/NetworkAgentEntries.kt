package io.thernal.console.network.ui.agent

import io.thernal.console.agent.AgentEntries
import io.thernal.console.agent.agentEntries
import io.thernal.console.agent.describe
import io.thernal.console.network.NetworkLog
import io.thernal.console.network.SensitiveHeaders

/**
 * What the network addon shares with an AI agent: how network logs read in every log feed, a `network` feed with
 * one record per call, and per-host counts. Headers are already masked at capture with the live `sensitiveHeaders`
 * setting; [SensitiveHeaders.DEFAULT] names are also redacted where they appear in bodies and query strings.
 */
internal fun networkAgentEntries(observer: NetworkCallObserver): AgentEntries {
    return agentEntries(
        id = "network",
        title = "Network",
        about = "HTTP calls the app made: method, URL, status, time, headers and bodies",
    ) {
        describe<NetworkLog.Request>(summary = ::networkSummary, detail = ::networkDetail, fields = ::networkFields)
        describe<NetworkLog.Response>(summary = ::networkSummary, detail = ::networkDetail, fields = ::networkFields)
        feed(
            name = "network",
            description = "One record per HTTP call, request and response together, under the call's id",
            records = observer.calls,
        )
        tool(name = "network_hosts", description = "Hosts the app called, with call and failure counts") {
            run { networkHostsText(observer.hosts.value.values) }
        }
        redact(*SensitiveHeaders.DEFAULT.names.toTypedArray())
    }
}

private fun networkHostsText(hosts: Collection<NetworkHostStats>): String {
    if (hosts.isEmpty()) return "No HTTP calls since the app started."
    return hosts.sortedByDescending { it.calls }.joinToString(separator = "\n") { stats ->
        val average = if (stats.timedCalls > 0) " · avg ${stats.totalDurationMs / stats.timedCalls} ms" else ""
        val last = stats.lastStatus?.toString() ?: "none"
        "${stats.host} · ${stats.calls} calls · ${stats.failures} failed · last status $last$average"
    }
}
