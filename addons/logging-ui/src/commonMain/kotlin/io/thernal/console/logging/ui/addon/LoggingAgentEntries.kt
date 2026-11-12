package io.thernal.console.logging.ui.addon

import io.thernal.console.agent.AgentEntries
import io.thernal.console.agent.agentEntries
import io.thernal.console.agent.enum
import io.thernal.console.core.log.LogLevel
import io.thernal.console.logging.ui.ConsoleLogObserver
import io.thernal.console.runtime.console.Console

/** What the logging addon shares with an AI agent: every log as a feed, a summary of the list, and writing a log. */
internal fun loggingAgentEntries(): AgentEntries {
    return agentEntries(
        id = "logging",
        title = "Logs",
        about = "Every log the app writes: level, tag, message, full text by id",
    ) {
        logFeed(name = "logs", description = "Every log the app wrote, oldest first", logs = ConsoleLogObserver.stream)
        tool(
            name = "logs_summary",
            description = "Logs kept in the app per level, kind and tag, and whether logging is on",
        ) {
            run { logsSummary(logs = ConsoleLogObserver.logs.value, config = ConsoleLogObserver.config.value) }
        }
        action(
            name = "logs_write",
            description = "Writes a log of kind AgentLog: a marker between the steps of a test, or a log of any " +
                "level and tag for testing what reacts to logs",
        ) {
            val message = text(key = "message", description = "Log message")
            val level = enum(key = "level", description = "Log level", default = LogLevel.Info)
            val tag = text(key = "tag", description = "Log tag; empty for none", default = AgentLog.DEFAULT_TAG)
            run { call -> writeAgentLog(message = call[message], level = call[level], tag = call[tag]) }
        }
    }
}

/** Writes through `Console.notify`, which does not wait, so a log held by the stepper cannot stall the call. */
private fun writeAgentLog(
    message: String,
    level: LogLevel,
    tag: String,
): String {
    require(message.isNotBlank()) { "Pass a non-empty message" }
    check(Console.isEnabled) { "Console is off, so the log was not written" }
    val log = AgentLog(message = message, tag = tag.trim().ifEmpty { null }, level = level)
    Console.notify { log }
    val written = "Wrote ${level.name} log ${log.id}; `logs_get` with this id shows its seq."
    return if (ConsoleLogObserver.config.value.enabled) {
        written
    } else {
        "$written Logging is off, so it is neither listed in the app nor shared."
    }
}
