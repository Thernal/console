package io.thernal.console.mcp.hub

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readByteArray
import io.ktor.utils.io.readInt
import io.ktor.utils.io.writeFully
import io.ktor.utils.io.writeInt

/** Largest frame the hub accepts; matches the app side. */
const val MAX_FRAME_BYTES = 4 * 1024 * 1024

/** Reads one frame: a 4-byte big-endian length, then that many UTF-8 bytes. */
suspend fun ByteReadChannel.readFrame(): String {
    val size = readInt()
    require(size in 0..MAX_FRAME_BYTES) { "Invalid frame size $size" }
    return readByteArray(size).decodeToString()
}

/** Writes [json] as one frame. */
suspend fun ByteWriteChannel.writeFrame(json: String) {
    val bytes = json.encodeToByteArray()
    writeInt(bytes.size)
    writeFully(bytes)
    flush()
}
