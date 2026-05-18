package az.theternal.console.model

import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface Log {
    val id: String
    val timestamp: Long
    val tag: String?
    val level: LogLevel
    val message: String

    fun copyWith(
        message: String = this.message,
        tag: String? = this.tag,
    ): Log

    @OptIn(ExperimentalUuidApi::class)
    data class Basic(
        override val message: String,
        override val tag: String? = null,
        override val level: LogLevel = LogLevel.None,
        override val id: String = Uuid.random().toString(),
        override val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    ) : Log {
        override fun copyWith(
            message: String,
            tag: String?,
        ): Log = copy(message = message, tag = tag)
    }

    companion object {
        operator fun invoke(
            message: String,
            tag: String? = null,
            level: LogLevel = LogLevel.None,
        ): Log = Basic(message, tag, level)
    }
}
