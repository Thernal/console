package io.thernal.console.settings.ui.store

private const val FORMAT_HEADER = "console-settings v1"

/** Plain `key=value` lines behind a version header; an unrecognized header discards the file. */
internal object SettingsFileFormat {

    fun encode(entries: Map<String, String>): ByteArray {
        val lines = buildList {
            add(FORMAT_HEADER)
            entries.forEach { (key, value) -> add("$key=$value") }
        }
        return lines.joinToString(separator = "\n").encodeToByteArray()
    }

    fun decode(bytes: ByteArray): Map<String, String>? {
        val lines = bytes.decodeToString().split("\n")
        if (lines.firstOrNull() != FORMAT_HEADER) return null
        return lines.drop(1).mapNotNull(::parseLine).toMap()
    }

    private fun parseLine(line: String): Pair<String, String>? {
        val separatorIndex = line.indexOf('=')
        if (separatorIndex <= 0) return null
        return line.substring(0, separatorIndex) to line.substring(separatorIndex + 1)
    }
}
