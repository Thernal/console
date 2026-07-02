package io.thernal.console.crash.ui.view.sessions.model

import io.thernal.console.ui.core.ViewIntent

internal sealed interface CrashSessionsIntent : ViewIntent {

    data class SetShowSafe(val showSafe: Boolean) : CrashSessionsIntent

    /** First delete tap: arms the row's confirm state instead of deleting. */
    data class ArmDelete(val sessionId: String) : CrashSessionsIntent

    /** Second tap on an armed row: performs the destructive delete. */
    data class ConfirmDelete(val sessionId: String) : CrashSessionsIntent

    data object DisarmDelete : CrashSessionsIntent
}
