package az.theternal.console.debugstepper

import az.theternal.console.core.base.Log

data class DebugStepperState(
    val enabled: Boolean = false,
    val paused: Boolean = false,
    val events: List<Log> = emptyList(),
    val pendingLogs: Int = 0,
    val currentStep: String? = null,
    val blockedTag: String? = null,
)
