package io.thernal.console.agent

@Suppress("UnusedParameter")
fun agentEntries(
    id: String,
    title: String,
    about: String,
    block: AgentEntriesScope.() -> Unit,
): AgentEntries = AgentEntries()
