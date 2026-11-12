package io.thernal.console.agent

/**
 * One entry of an [AgentFeed]. [summary] is the one line an agent sees in a list; [detail] is the full text it reads
 * on request. [level], [tag] and [kind] let the agent filter; [fields] carry structured values such as a status code.
 */
data class AgentRecord(
    val id: String,
    val timeMillis: Long,
    val summary: String,
    val detail: String? = null,
    val level: String? = null,
    val tag: String? = null,
    val kind: String? = null,
    val fields: Map<String, String> = emptyMap(),
)
