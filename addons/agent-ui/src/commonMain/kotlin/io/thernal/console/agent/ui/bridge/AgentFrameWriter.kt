package io.thernal.console.agent.ui.bridge

import io.ktor.utils.io.ByteWriteChannel
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/** Serializes frames from the several coroutines that write to one connection (records, manifests, results). */
internal class AgentFrameWriter(private val channel: ByteWriteChannel) {
    private val mutex = Mutex()

    suspend fun write(json: String) {
        mutex.withLock { channel.writeFrame(json) }
    }
}
