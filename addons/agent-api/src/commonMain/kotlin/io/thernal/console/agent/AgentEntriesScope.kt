package io.thernal.console.agent

import io.thernal.console.core.log.Log
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

/** The DSL of [agentEntries]. */
interface AgentEntriesScope {
    /** A read-only tool: the agent asks, the app answers from its current state. */
    fun tool(
        name: String,
        description: String,
        block: AgentToolScope.() -> Unit,
    )

    /** A tool that changes the app. Offered only while [AgentConfig.enableActions] is on. */
    fun action(
        name: String,
        description: String,
        block: AgentToolScope.() -> Unit,
    )

    /** A stream the app pushes as things happen; see [AgentFeed]. */
    fun feed(
        name: String,
        description: String,
        records: Flow<AgentRecord>,
    )

    /** A feed of Console logs; each log becomes a record through the format registered for its type. */
    fun logFeed(
        name: String,
        description: String,
        logs: Flow<Log>,
    )

    /** How logs of exactly [type] look to an agent, in every log feed. */
    fun <T : Log> describe(
        type: KClass<T>,
        summary: (T) -> String,
        detail: (T) -> String?,
        fields: (T) -> Map<String, String>,
    )

    /**
     * Keys whose values never leave the app: in JSON (`"token": "…"`), in query strings and forms (`token=…`) and in
     * header lines (`Authorization: …`). A key matches every name that contains it, ignoring case.
     */
    fun redact(vararg keys: String)
}

/** [AgentEntriesScope.describe] for [T]; by default the first message line, the share text and no fields. */
inline fun <reified T : Log> AgentEntriesScope.describe(
    noinline summary: (T) -> String = { it.message.lineSequence().firstOrNull().orEmpty() },
    noinline detail: (T) -> String? = { it.toShareText() },
    noinline fields: (T) -> Map<String, String> = { emptyMap() },
) {
    describe(type = T::class, summary = summary, detail = detail, fields = fields)
}
