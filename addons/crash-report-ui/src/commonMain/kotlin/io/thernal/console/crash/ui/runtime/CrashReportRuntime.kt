package io.thernal.console.crash.ui.runtime

import io.thernal.console.crash.ui.store.CrashFileSystem
import io.thernal.console.crash.ui.store.CrashStore
import kotlin.concurrent.Volatile
import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Holds the process-wide [CrashStore] and the current streaming session. Started once from the
 * addon's `onInstall`; a missing platform directory (e.g. Android before the auto-init `Context`
 * arrives) leaves the store `null` and every consumer silently no-ops.
 */
internal object CrashReportRuntime {

    @Volatile
    private var activeStore: CrashStore? = null

    val store: CrashStore? get() = activeStore

    fun start() {
        val directoryPath = CrashFileSystem.defaultBaseDirectoryPath() ?: return
        startIn(directoryPath)
    }

    @OptIn(ExperimentalUuidApi::class)
    fun startIn(directoryPath: String) {
        if (activeStore != null) return
        val store = CrashStore(directoryPath)
        val isStarted = store.startSession(
            id = Uuid.random().toString(),
            startedAtMs = Clock.System.now().toEpochMilliseconds(),
        )
        if (isStarted) activeStore = store
    }

    /** Closes the active session and detaches the store; used by tests. */
    fun reset() {
        activeStore?.closeSession()
        activeStore = null
    }
}
