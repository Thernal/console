package az.theternal.console.debugstepper

import az.theternal.console.runtime.model.Log
import az.theternal.console.runtime.model.LogLevel

data class DebugStepperState(
    val enabled: Boolean = false,
    val paused: Boolean = false,
    val pauseOnMatch: Boolean = false,
    val pauseOnTags: Set<String> = emptySet(),
    val pauseOnLevelAtLeast: LogLevel? = null,
    val autoResumeSeconds: Int? = null,
    val steppedEvents: List<Log> = emptyList(),
    val pendingLogs: Int = 0,
    val blockedLogId: String? = null,
    val blockedTag: String? = null,
)
