package io.thernal.console.agent

import kotlinx.coroutines.flow.Flow

/**
 * A stream the app pushes to the hub as things happen, so the agent can still read it after the app crashed and can
 * wait for an entry. The hub derives three tools from it: `<name>` (filtered list), `<name>_get` (one entry in full)
 * and `<name>_wait` (wait for a matching entry).
 *
 * [records] should be a hot flow whose producer never suspends (a shared flow that drops the oldest entries), so a
 * slow or missing hub cannot slow the app down. A replay buffer lets the bridge pick up entries emitted before it
 * started collecting.
 */
class AgentFeed internal constructor(
    val name: String,
    val description: String,
    val records: Flow<AgentRecord>,
)
