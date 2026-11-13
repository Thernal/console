package io.thernal.console.crash.ui.agent

import io.thernal.console.agent.AgentEntries
import io.thernal.console.agent.AgentRecord
import io.thernal.console.agent.agentEntries
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.crash.ui.session.CrashSessionClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val DEFAULT_SESSIONS = 10
private const val MAX_SESSIONS = 100
private const val DEFAULT_LOGS = 30
private const val MAX_LOGS = 500
private const val CRASH_TAG = "Crash"
private const val CRASH_KIND = "CrashSession"

/**
 * What the crash report addon shares with an AI agent: saved sessions (they survive relaunches, so a crash can be
 * read on the next launch even when no hub ran at the time), one `crashes` record when the app starts after a crash,
 * and deleting sessions while actions are on.
 */
internal fun crashAgentEntries(): AgentEntries {
    return agentEntries(
        id = "crash-report",
        title = "Crash reports",
        about = "Sessions saved on the device, with the stack trace and the logs before a crash; " +
            "they survive relaunches",
    ) {
        feed(
            name = "crashes",
            description = "One record when the app starts after its previous run crashed",
            records = previousCrash(),
        )
        tool(name = "crash_sessions", description = "Saved sessions, newest first; the running one is not listed") {
            val crashedOnly = toggle(
                key = "crashed_only",
                description = "Only sessions that crashed or probably crashed",
                default = true,
            )
            val limit = int(
                key = "limit",
                description = "How many sessions",
                default = DEFAULT_SESSIONS,
                min = 1,
                max = MAX_SESSIONS,
            )
            run { call -> crashSessionsText(crashedOnly = call[crashedOnly], limit = call[limit]) }
        }
        tool(name = "crash_session", description = "One session: how it ended, the stack trace and the logs before") {
            val id = text(key = "id", description = "Session id from crash_sessions")
            val logs = int(
                key = "logs",
                description = "How many logs before the end",
                default = DEFAULT_LOGS,
                min = 0,
                max = MAX_LOGS,
            )
            val fullLogs = toggle(key = "full_logs", description = "Each log's full text instead of one line")
            run { call -> crashSessionText(id = call[id], logCount = call[logs], fullLogs = call[fullLogs]) }
        }
        action(name = "crash_delete", description = "Deletes one saved session") {
            val id = text(key = "id", description = "Session id from crash_sessions")
            run { call -> deleteSession(call[id]) }
        }
        action(name = "crash_clear", description = "Deletes every saved session; the running one stays") {
            run {
                CrashReports.clearAll()
                "Deleted every saved session."
            }
        }
    }
}

/** The newest saved session is the previous run; when it crashed, the feed gets one record for it. */
private fun previousCrash(): Flow<AgentRecord> {
    return flow {
        CrashReports.refreshSessions()
        val previous = CrashReports.sessions.value.firstOrNull() ?: return@flow
        if (previous.classification == CrashSessionClass.Safe) return@flow
        val what = if (previous.classification == CrashSessionClass.Confirmed) {
            "The previous run crashed: ${previous.summary.orEmpty()}"
        } else {
            "The previous run probably crashed: no trace (native crash, OOM kill or ANR)"
        }
        emit(
            AgentRecord(
                id = previous.id,
                timeMillis = previous.crashedAtMs ?: previous.startedAtMs,
                summary = what,
                detail = crashSessionText(id = previous.id, logCount = DEFAULT_LOGS, fullLogs = false),
                level = crashLevel(previous.classification).name,
                tag = CRASH_TAG,
                kind = CRASH_KIND,
            ),
        )
    }
}

private fun deleteSession(id: String): String {
    CrashReports.refreshSessions()
    require(CrashReports.sessions.value.any { it.id == id }) { "No saved session \"$id\"; crash_sessions lists them" }
    CrashReports.delete(id)
    return "Deleted session $id."
}
