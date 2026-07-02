package io.thernal.console.crash.wire

/**
 * Cursor-based reader for buffers produced by [ByteWriter]. [readRecord] returns `null` at a
 * truncated final record, letting callers tolerate a torn tail.
 */
internal class ByteReader(private val bytes: ByteArray) {

    private var position = 0

    val remaining: Int get() = bytes.size - position

    fun readInt(): Int {
        var result = 0
        repeat(INT_BYTES) {
            result = result shl BITS_PER_BYTE or (bytes[position++].toInt() and BYTE_MASK)
        }
        return result
    }

    fun readLong(): Long {
        var result = 0L
        repeat(LONG_BYTES) {
            result = result shl BITS_PER_BYTE or (bytes[position++].toLong() and BYTE_MASK_LONG)
        }
        return result
    }

    fun readString(): String? {
        val size = readInt()
        if (size == ByteWriter.NULL_LENGTH) return null
        val value = bytes.decodeToString(startIndex = position, endIndex = position + size)
        position += size
        return value
    }

    fun readStringMap(): Map<String, String> {
        val count = readInt()
        val map = LinkedHashMap<String, String>(count)
        repeat(count) {
            val key = readString().orEmpty()
            val value = readString().orEmpty()
            map[key] = value
        }
        return map
    }

    /**
     * Reads a length-prefixed record, or returns `null` when no complete record remains — either
     * fewer than a length prefix, a negative length, or a body cut short by truncation. The cursor is
     * left unmoved in that case.
     */
    fun readRecord(): ByteArray? {
        if (remaining < INT_BYTES) return null
        val mark = position
        val size = readInt()
        if (size < 0 || size > remaining) {
            position = mark
            return null
        }
        val record = bytes.copyOfRange(fromIndex = position, toIndex = position + size)
        position += size
        return record
    }

    companion object {
        private const val BYTE_MASK = 0xFF
        private const val BYTE_MASK_LONG = 0xFFL
        private const val INT_BYTES = 4
        private const val LONG_BYTES = 8
        private const val BITS_PER_BYTE = 8
    }
}
