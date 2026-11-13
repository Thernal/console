package io.thernal.console.agent

/** One argument of an [AgentTool], declared through [AgentToolScope]; the hub turns it into the MCP input schema. */
sealed interface AgentParam {
    val key: String
    val description: String
    val isRequired: Boolean

    data class Text(
        override val key: String,
        override val description: String,
        override val isRequired: Boolean,
        val default: String?,
    ) : AgentParam

    data class Integer(
        override val key: String,
        override val description: String,
        override val isRequired: Boolean,
        val default: Int?,
        val min: Int,
        val max: Int,
    ) : AgentParam

    data class Toggle(
        override val key: String,
        override val description: String,
        val default: Boolean,
    ) : AgentParam {
        override val isRequired: Boolean get() = false
    }

    data class Choice(
        override val key: String,
        override val description: String,
        override val isRequired: Boolean,
        val options: List<String>,
        val default: String?,
    ) : AgentParam
}
