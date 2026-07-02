package io.thernal.console.crash.ui.view.sessions.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.ui.core.IntentHandler
import io.thernal.console.ui.core.StateHolder
import kotlinx.coroutines.launch

internal class CrashSessionsViewModel :
    ViewModel(),
    StateHolder,
    IntentHandler<CrashSessionsIntent> {

    val state = CrashSessionsState()

    override val handler = onIntentUpdate { intent ->
        when (intent) {
            is CrashSessionsIntent.SetShowSafe -> state.showSafe.set(intent.showSafe)
            is CrashSessionsIntent.ArmDelete -> state.armedDeleteId.set(intent.sessionId)
            is CrashSessionsIntent.ConfirmDelete -> deleteSession(intent.sessionId)
            CrashSessionsIntent.DisarmDelete -> state.armedDeleteId.set(null)
        }
    }

    init {
        CrashReports.refreshSessions()
        viewModelScope.launch {
            CrashReports.sessions.collect { state.sessions.set(it) }
        }
    }

    private fun deleteSession(sessionId: String) {
        CrashReports.delete(sessionId)
        state.armedDeleteId.set(null)
    }
}
