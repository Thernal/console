package io.thernal.console.crash.ui.view.detail.model

import androidx.lifecycle.ViewModel
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.session.CrashSidecar
import io.thernal.console.ui.core.StateHolder

internal class CrashSessionDetailViewModel(
    private val sessionId: String,
) : ViewModel(),
    StateHolder {

    val state = CrashSessionDetailState()

    init {
        load()
    }

    fun shareText(): String? {
        return CrashReports.share(sessionId)
    }

    private fun load() {
        val session = CrashReports.open(sessionId)
        val sidecar = CrashReportRuntime.store?.readCrashSidecar(sessionId)?.let(CrashSidecar::parse)

        snapshot {
            // Newest first, matching the live Logs view — the fatal crash record lands on top.
            state.logs.set(session?.logs.orEmpty().asReversed())
            state.crashSummary.set(sidecar?.summary)
            state.isLoaded.set(true)
        }
    }
}
