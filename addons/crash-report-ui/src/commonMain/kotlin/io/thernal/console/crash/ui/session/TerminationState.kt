package io.thernal.console.crash.ui.session

/**
 * Last observed process state, persisted to the `.state` sidecar only on rare lifecycle
 * transitions. A missing sidecar means the app died before any transition — treated as
 * foreground for classification.
 */
internal enum class TerminationState {
    FOREGROUND,
    BACKGROUND,

    /** Clean exit signal — only reliably available on desktop (JVM shutdown hook). */
    CLEAN,
}
