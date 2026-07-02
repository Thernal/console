package io.thernal.console.crash.ui.lifecycle

import io.thernal.console.crash.ui.session.TerminationState

/**
 * Installs dependency-free foreground/background tracking, invoking [onStateChanged] only on
 * transitions: Android `registerActivityLifecycleCallbacks`, iOS `NSNotificationCenter`
 * foreground/background notifications, JVM a shutdown hook reporting [TerminationState.CLEAN]
 * (a real clean-exit signal exists on desktop only).
 */
internal expect fun installLifecycleTracking(onStateChanged: (TerminationState) -> Unit)
