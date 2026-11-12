package io.thernal.console.stepper.ui.agent

import io.thernal.console.core.log.Log
import io.thernal.console.stepper.ui.stepper.Stepper

private const val MAX_SUMMARY_LENGTH = 200

/** The `stepper_state` answer: on or off, paused, the rules, and the log held now. */
internal fun stepperStateText(
    config: Stepper.Config,
    state: Stepper.State,
    pausedByAgent: Boolean,
): String {
    val lines = mutableListOf<String>()
    lines += when {
        !config.enabled -> "Stepper off: logs pass through."
        !config.paused -> "Stepper on, not paused: logs pass through. Rules: ${rulesText(config)}."
        else -> "Stepper paused: holds ${rulesText(config)}."
    }
    if (config.paused) {
        val resume = config.autoResumeSeconds?.let { "each held log is released after $it s" } ?: "off"
        lines += "Auto-resume: $resume"
        lines += if (pausedByAgent) {
            "Paused by an agent: it resumes by itself when the hub disconnects."
        } else {
            "Paused in the app, not by an agent."
        }
    }
    lines += heldText(state)
    lines += "Logs held or queued in the stepper: ${state.pendingLogs}"
    return lines.joinToString(separator = "\n")
}

/** Which logs the stepper holds while paused. */
internal fun rulesText(config: Stepper.Config): String {
    if (!config.pauseOnMatch) return "every log"
    val tags = config.pauseOnTags.takeIf { it.isNotEmpty() }?.sorted()?.joinToString(prefix = "tagged ")
    val level = config.pauseOnLevelAtLeast?.let { "at level ${it.name} or above" }
    return listOfNotNull("logs", tags, level).joinToString(separator = " ")
}

/** The held log, from the stepper's own list of held logs. */
internal fun heldText(state: Stepper.State): String {
    val id = state.blockedLogId ?: return "Nothing held now."
    val log = state.steppedEvents.lastOrNull { it.id == id }
    val text = log?.let(::logLine) ?: "log $id${state.blockedTag?.let { " [$it]" }.orEmpty()}"
    return "Held now: $text"
}

/** One line for [log]: id, level, tag and the first line of its message. */
internal fun logLine(log: Log): String {
    val tag = log.tag?.takeIf(String::isNotBlank)?.let { " [$it]" }.orEmpty()
    return "${log.id} ${log.level.name}$tag ${logSummary(log)}"
}

/** The first line of [log]'s message, cut to a readable length. */
internal fun logSummary(log: Log): String {
    val first = log.message.lineSequence().firstOrNull().orEmpty().trim()
    return if (first.length > MAX_SUMMARY_LENGTH) first.take(MAX_SUMMARY_LENGTH) + "…" else first
}
