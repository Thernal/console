package io.thernal.console.agent.ui.bridge

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Ring buffer of the most recent entries, replayed to a hub that connects after they were captured. */
internal class AgentHistory(capacity: Int) {
    private val mutex = Mutex()
    private val entries = ArrayDeque<AgentEntry>()
    private var capacity = capacity.coerceAtLeast(1)

    suspend fun append(entry: AgentEntry) {
        mutex.withLock {
            entries.addLast(entry)
            trim()
        }
    }

    suspend fun resize(capacity: Int) {
        mutex.withLock {
            this.capacity = capacity.coerceAtLeast(1)
            trim()
        }
    }

    suspend fun snapshot(): List<AgentEntry> {
        return mutex.withLock { entries.toList() }
    }

    private fun trim() {
        while (entries.size > capacity) {
            entries.removeFirst()
        }
    }
}
