package io.thernal.console.logging.ui.addon

import io.thernal.console.core.log.Log
import io.thernal.console.core.log.LogLevel
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * A log an AI agent wrote with `logs_write`. It has its own type so the hub's `kind` filter tells it from the app's
 * logs whatever its tag; in the Logs tab it renders like a `BasicLog`.
 */
@OptIn(ExperimentalUuidApi::class)
internal data class AgentLog(
    override val message: String,
    override val tag: String?,
    override val level: LogLevel,
    override val id: String = Uuid.random().toString(),
    override val timestamp: Instant = Clock.System.now(),
    override val groupId: String? = null,
) : Log {
    companion object {
        const val DEFAULT_TAG = "agent"
    }
}
