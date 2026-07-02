package io.thernal.console.crash.ui.view.detail.model

import androidx.lifecycle.ViewModel
import io.thernal.console.crash.ui.CrashReports
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.session.CrashSidecar
import io.thernal.console.crash.ui.session.partitionFatal
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
        val (preceding, fatal) = session?.logs.orEmpty().partitionFatal()

        snapshot {
            state.precedingLogs.set(preceding)
            state.crashSummary.set(sidecar?.summary)
            state.stackTrace.set(sidecar?.stackTrace ?: fatal?.message)
            state.crashedAtMs.set(sidecar?.crashedAtMs)
            state.isLoaded.set(true)
        }
    }
}
