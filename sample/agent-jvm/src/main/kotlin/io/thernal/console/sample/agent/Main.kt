@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.sample.agent

import io.thernal.console.agent.AgentRecord
import io.thernal.console.agent.AgentRegistry
import io.thernal.console.agent.ConsoleAgent
import io.thernal.console.agent.agentEntries
import io.thernal.console.api.autoinit.runConsoleInitializers
import io.thernal.console.core.ConsoleInternalApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlin.time.Clock

private const val DEFAULT_SECONDS = 30
private const val TICK_MILLIS = 1_000L
private const val REPLAY = 100
private const val MAX_COUNTER = 1_000
private const val ACTIONS_FLAG = "--actions"
private const val ACTIONS_AFTER_FLAG = "--actions-after"

private val events = MutableSharedFlow<AgentRecord>(replay = REPLAY, onBufferOverflow = BufferOverflow.DROP_OLDEST)

@Volatile
private var counter = 0

/**
 * Headless JVM app for the agent bridge. It registers its own agent entries, as a project-specific addon would: an
 * `events` feed, a `sample_state` tool and a `sample_reset` action. Arguments: the number of seconds to run,
 * `--actions` to allow actions from the start, `--actions-after <seconds>` to allow them later.
 */
fun main(args: Array<String>) {
    val seconds = args.firstOrNull()?.toIntOrNull() ?: DEFAULT_SECONDS
    val actionsAfterIndex = args.indexOf(ACTIONS_AFTER_FLAG).takeIf { it >= 0 }
    val actionsAfter = actionsAfterIndex?.let { args.getOrNull(it + 1)?.toIntOrNull() }
    ConsoleAgent.updateConfig { copy(enableActions = ACTIONS_FLAG in args) }
    AgentRegistry.register(sampleEntries())
    runConsoleInitializers()

    emit(summary = "Sample started", level = "Info")
    emit(
        summary = "Token refresh failed: 401",
        level = "Warning",
        detail = """{"refreshToken": "abc123", "user": "ali"}""",
    )
    repeat(seconds) { tick ->
        Thread.sleep(TICK_MILLIS)
        counter++
        emit(summary = "Tick ${tick + 1} of $seconds, counter $counter", level = "Verbose")
        if (actionsAfter == tick + 1) ConsoleAgent.updateConfig { copy(enableActions = true) }
    }
    emit(summary = "Sample finished", level = "Success")
    Thread.sleep(TICK_MILLIS)
}

private fun sampleEntries() =
    agentEntries(id = "sample", title = "Sample", about = "A demo app that counts up once a second") {
        feed(name = "events", description = "What the sample app does, as it happens", records = events)
        tool(name = "sample_state", description = "The counter value") {
            run { "counter=$counter" }
        }
        action(name = "sample_reset", description = "Sets the counter") {
            val value = int(key = "value", description = "New counter value", default = 0, min = 0, max = MAX_COUNTER)
            run { call ->
                counter = call[value]
                emit(summary = "Counter reset to ${call[value]} by an agent", level = "Info")
                "Counter is now ${call[value]}"
            }
        }
        redact("refresh")
    }

private fun emit(
    summary: String,
    level: String,
    detail: String? = null,
) {
    events.tryEmit(
        AgentRecord(
            id = "e${Clock.System.now().toEpochMilliseconds()}-$counter",
            timeMillis = Clock.System.now().toEpochMilliseconds(),
            summary = summary,
            detail = detail,
            level = level,
            tag = "Sample",
        ),
    )
}
