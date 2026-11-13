package io.thernal.console.agent

import io.thernal.console.core.log.Log
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

/**
 * Same DSL receiver as `agent-api`, kept so an addon's `block` lambda (never invoked in the no-op path) still
 * type-checks unchanged between build variants.
 */
interface AgentEntriesScope {
    fun tool(
        name: String,
        description: String,
        block: AgentToolScope.() -> Unit,
    )

    fun action(
        name: String,
        description: String,
        block: AgentToolScope.() -> Unit,
    )

    fun feed(
        name: String,
        description: String,
        records: Flow<AgentRecord>,
    )

    fun logFeed(
        name: String,
        description: String,
        logs: Flow<Log>,
    )

    fun <T : Log> describe(
        type: KClass<T>,
        summary: (T) -> String,
        detail: (T) -> String?,
        fields: (T) -> Map<String, String>,
    )

    fun redact(vararg keys: String)
}

inline fun <reified T : Log> AgentEntriesScope.describe(
    noinline summary: (T) -> String = { it.message.lineSequence().firstOrNull().orEmpty() },
    noinline detail: (T) -> String? = { it.toShareText() },
    noinline fields: (T) -> Map<String, String> = { emptyMap() },
) {
    describe(type = T::class, summary = summary, detail = detail, fields = fields)
}
