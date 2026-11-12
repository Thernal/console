package io.thernal.console.agent

/** What one addon shares with AI agents; built with [agentEntries] and registered in [AgentRegistry]. */
class AgentEntries internal constructor(
    val id: String,
    val title: String,
    val about: String,
    val tools: List<AgentTool>,
    val feeds: List<AgentFeed>,
    val logFormats: List<AgentLogFormat<*>>,
    val redactedKeys: Set<String>,
) {
    /** Every hub tool name these entries occupy, including the ones the hub derives from feeds. */
    internal fun toolNames(): List<String> {
        return tools.map { it.name } + feeds.flatMap { feed ->
            listOf(feed.name, feed.name + AgentNames.FEED_GET_SUFFIX, feed.name + AgentNames.FEED_WAIT_SUFFIX)
        }
    }
}
