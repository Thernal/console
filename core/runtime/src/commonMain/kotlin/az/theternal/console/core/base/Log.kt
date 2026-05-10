package az.theternal.console.core.base

import kotlin.time.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
abstract class Log {
    val id: String = Uuid.random().toString()
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
    open val tag: String? = null
    abstract val message: String

    companion object {
        operator fun invoke(
            message: String,
            tag: String? = null,
        ): Log {
            return object : Log() {
                override val tag: String? = tag
                override val message: String = message
            }
        }
    }
}
