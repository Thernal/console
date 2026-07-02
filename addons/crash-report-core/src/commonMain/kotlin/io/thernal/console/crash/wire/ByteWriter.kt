package io.thernal.console.crash.wire

/**
 * Minimal growable big-endian byte buffer for the hand-rolled wire format. Strings are
 * length-prefixed UTF-8 (a `-1` length marks `null`), so arbitrary content — HTTP bodies, newlines —
 * needs no escaping.
 */
internal class ByteWriter(initialCapacity: Int = DEFAULT_CAPACITY) {

    private var buffer = ByteArray(initialCapacity)
    private var length = 0

    private fun ensureCapacity(additional: Int) {
        val required = length + additional
        if (required <= buffer.size) return
        var newSize = buffer.size
        while (newSize < required) {
            newSize *= GROWTH_FACTOR
        }
        buffer = buffer.copyOf(newSize)
    }

    fun writeInt(value: Int): ByteWriter {
        ensureCapacity(INT_BYTES)
        var shift = (INT_BYTES - 1) * BITS_PER_BYTE
        repeat(INT_BYTES) {
            buffer[length++] = (value ushr shift).toByte()
            shift -= BITS_PER_BYTE
        }
        return this
    }

    fun writeLong(value: Long): ByteWriter {
        ensureCapacity(LONG_BYTES)
        var shift = (LONG_BYTES - 1) * BITS_PER_BYTE
        repeat(LONG_BYTES) {
            buffer[length++] = (value ushr shift).toByte()
            shift -= BITS_PER_BYTE
        }
        return this
    }

    fun writeString(value: String?): ByteWriter {
        if (value == null) {
            return writeInt(NULL_LENGTH)
        }
        val encoded = value.encodeToByteArray()
        writeInt(encoded.size)
        return writeBytes(encoded)
    }

    fun writeStringMap(map: Map<String, String>): ByteWriter {
        writeInt(map.size)
        for ((key, value) in map) {
            writeString(key)
            writeString(value)
        }
        return this
    }

    /** Appends [bytes] as a self-delimiting record: a length prefix followed by the bytes. */
    fun writeRecord(bytes: ByteArray): ByteWriter {
        writeInt(bytes.size)
        return writeBytes(bytes)
    }

    fun toByteArray(): ByteArray {
        return buffer.copyOf(length)
    }

    private fun writeBytes(bytes: ByteArray): ByteWriter {
        ensureCapacity(bytes.size)
        bytes.copyInto(destination = buffer, destinationOffset = length)
        length += bytes.size
        return this
    }

    companion object {
        /** Length prefix that encodes a `null` string. */
        const val NULL_LENGTH: Int = -1

        private const val DEFAULT_CAPACITY = 64
        private const val GROWTH_FACTOR = 2
        private const val INT_BYTES = 4
        private const val LONG_BYTES = 8
        private const val BITS_PER_BYTE = 8
    }
}
