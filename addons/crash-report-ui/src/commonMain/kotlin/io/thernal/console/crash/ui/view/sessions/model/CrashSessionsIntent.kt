package io.thernal.console.crash.ui.view.sessions.model

import io.thernal.console.ui.core.ViewIntent

internal sealed interface CrashSessionsIntent : ViewIntent {

    data class SetShowSafe(val showSafe: Boolean) : CrashSessionsIntent

    /**
     * Deletes a past session. The swipe-to-reveal gesture is the destructive confirm — the
     * delete button only becomes tappable after the row is deliberately swiped open.
     */
    data class Delete(val sessionId: String) : CrashSessionsIntent
}
