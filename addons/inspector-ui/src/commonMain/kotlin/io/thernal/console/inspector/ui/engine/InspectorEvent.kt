package io.thernal.console.inspector.ui.engine

/** Raised on composition threads by [RecompositionObserver], consumed on the main thread. */
internal sealed interface InspectorEvent {
    /** A scope finished a pass. [wasSkipped] is null when the runtime cannot say. */
    data class ScopePassed(
        val identity: Any,
        val wasSkipped: Boolean?,
    ) : InspectorEvent

    data class ScopeInvalidated(
        val identity: Any,
        val reason: String,
    ) : InspectorEvent

    data class CompositionTimed(val micros: Long) : InspectorEvent
}
