package io.thernal.console.agent

import io.thernal.console.core.log.Log
import kotlin.reflect.KClass

/** How logs of one exact [type] look to an agent; registered with `describe<T>()` by the addon that owns the type. */
class AgentLogFormat<T : Log> internal constructor(
    val type: KClass<T>,
    private val summary: (T) -> String,
    private val detail: (T) -> String?,
    private val fields: (T) -> Map<String, String>,
) {
    @Suppress("UNCHECKED_CAST")
    internal fun record(log: Log): AgentRecord {
        val typed = log as T
        return defaultRecord(log).copy(
            summary = summary(typed),
            detail = detail(typed),
            fields = fields(typed),
        )
    }

    internal companion object {
        private const val SUMMARY_LENGTH = 200

        /** A log as an agent sees it when no format is registered for its type. */
        fun defaultRecord(log: Log): AgentRecord {
            return AgentRecord(
                id = log.id,
                timeMillis = log.timestamp.toEpochMilliseconds(),
                summary = log.message.lineSequence().firstOrNull().orEmpty().take(SUMMARY_LENGTH),
                detail = log.toShareText(),
                level = log.level.name,
                tag = log.tag,
                kind = log::class.simpleName,
            )
        }
    }
}
