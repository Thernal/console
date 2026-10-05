package io.thernal.console.inspector.ui.engine.recomposition

import kotlin.time.TimeSource

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

    /** [composition] started a pass at [mark]; paired with [CompositionEnded] where the events are consumed. */
    data class CompositionStarted(
        val composition: Any,
        val mark: TimeSource.Monotonic.ValueTimeMark,
    ) : InspectorEvent

    data class CompositionEnded(
        val composition: Any,
        val mark: TimeSource.Monotonic.ValueTimeMark,
    ) : InspectorEvent
}
