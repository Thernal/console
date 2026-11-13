package io.thernal.console.agent

import io.thernal.console.core.ConsoleInternalApi

/**
 * Something an agent can call: a read-only tool, or an action when [isAction] is true. Actions change the app, so
 * the bridge only offers them while [AgentConfig.enableActions] is on.
 */
class AgentTool internal constructor(
    val name: String,
    val description: String,
    val params: List<AgentParam>,
    val isAction: Boolean,
    private val block: suspend (AgentCall) -> String,
) {
    @ConsoleInternalApi
    suspend fun run(call: AgentCall): String {
        return block(call)
    }
}
