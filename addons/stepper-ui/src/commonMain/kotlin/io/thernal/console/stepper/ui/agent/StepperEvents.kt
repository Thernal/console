package io.thernal.console.stepper.ui.agent

import io.thernal.console.agent.AgentRecord
import io.thernal.console.core.log.Log
import io.thernal.console.stepper.ui.stepper.Stepper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock

internal const val HELD_KIND = "Held"
internal const val RELEASED_KIND = "Released"

/**
 * One record when the stepper holds a log and one when it lets it go, from the stepper's [states]. A hold that starts
 * and ends between two reads of the state is missed; a hold that follows another at once yields both records.
 */
internal fun stepperEvents(states: Flow<Stepper.State>): Flow<AgentRecord> {
    return flow {
        var heldId: String? = null
        var heldLog: Log? = null
        var count = 0L
        states.collect { state ->
            val blocked = state.blockedLogId
            val previous = heldId
            if (previous != null && previous != blocked) {
                emit(stepRecord(RELEASED_KIND, previous, heldLog, number = count++))
                heldId = null
                heldLog = null
            }
            if (blocked != null && heldId == null) {
                heldId = blocked
                heldLog = state.steppedEvents.lastOrNull { it.id == blocked }
                emit(stepRecord(HELD_KIND, blocked, heldLog, number = count++, tag = state.blockedTag))
            }
        }
    }
}

private fun stepRecord(
    kind: String,
    logId: String,
    log: Log?,
    number: Long,
    tag: String? = log?.tag,
): AgentRecord {
    val text = log?.let(::logSummary) ?: "log $logId"
    return AgentRecord(
        id = "step-$number",
        timeMillis = Clock.System.now().toEpochMilliseconds(),
        summary = "$kind: $text",
        level = log?.level?.name,
        tag = tag ?: log?.tag,
        kind = kind,
        fields = buildMap {
            put("log_id", logId)
            log?.let { put("log_kind", it::class.simpleName.orEmpty()) }
        },
    )
}
