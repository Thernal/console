package az.theternal.console.runtime.model

import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

sealed interface Log {
    val id: String
    val timestamp: Long
    val tag: String?
    val level: LogLevel?
    val message: String

    @OptIn(ExperimentalUuidApi::class)
    data class Basic(
        override val message: String,
        override val tag: String? = null,
        override val level: LogLevel? = null,
        override val id: String = Uuid.random().toString(),
        override val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ) : Log

    companion object {
        operator fun invoke(
            message: String,
            tag: String? = "NO TAG",
            level: LogLevel? = null,
        ): Log = Basic(message, tag, level)
    }
}

fun Log.copyWith(
    message: String = this.message,
    tag: String? = this.tag,
): Log = Log.Basic(
    id = this.id,
    timestamp = this.timestamp,
    level = this.level,
    message = message,
    tag = tag,
)
