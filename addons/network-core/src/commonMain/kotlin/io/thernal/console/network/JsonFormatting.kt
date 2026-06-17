package io.thernal.console.network

private const val JSON_INDENT_UNIT = "  "
private val JSON_NUMBER_REGEX = Regex("""-?(0|[1-9]\d*)(\.\d+)?([eE][+-]?\d+)?""")

/**
 * Re-indents a JSON document for human-readable display. Returns `null` when the input is not a
 * valid JSON object or array, so callers can fall back to showing the raw body unchanged.
 *
 * Implemented as a recursive-descent reformatter that threads the read cursor through each helper
 * (returning the next index) so no shared mutable parser state is needed.
 */
internal fun String.toPrettyJsonOrNull(): String? {
    val source = trim()
    val first = source.firstOrNull()
    if (first != '{' && first != '[') {
        return null
    }

    return runCatching {
        val out = StringBuilder()
        val end = source.writeJsonValue(start = 0, out = out, depth = 0)
        require(source.skipWhitespace(end) == source.length) { "Trailing content after JSON value" }
        out.toString()
    }.getOrNull()
}

private fun String.skipWhitespace(from: Int): Int {
    var index = from
    while (index < length && this[index].isWhitespace()) index++
    return index
}

private fun StringBuilder.newLineIndent(depth: Int) {
    append('\n')
    repeat(depth) { append(JSON_INDENT_UNIT) }
}

private fun String.writeJsonValue(
    start: Int,
    out: StringBuilder,
    depth: Int,
): Int {
    val i = skipWhitespace(start)
    require(i < length) { "Unexpected end of input" }
    return when (this[i]) {
        '{' -> writeJsonObject(start = i, out = out, depth = depth)
        '[' -> writeJsonArray(start = i, out = out, depth = depth)
        '"' -> writeJsonString(start = i, out = out)
        else -> writeJsonPrimitive(start = i, out = out)
    }
}

private fun String.writeJsonObject(
    start: Int,
    out: StringBuilder,
    depth: Int,
): Int {
    var i = skipWhitespace(start + 1)
    if (i < length && this[i] == '}') {
        out.append("{}")
        return i + 1
    }

    out.append('{')
    while (true) {
        out.newLineIndent(depth + 1)
        i = skipWhitespace(i)
        require(i < length && this[i] == '"') { "Expected object key" }
        i = writeJsonString(start = i, out = out)
        i = skipWhitespace(i)
        require(i < length && this[i] == ':') { "Expected ':'" }
        out.append(": ")
        i = writeJsonValue(start = i + 1, out = out, depth = depth + 1)
        i = skipWhitespace(i)
        require(i < length) { "Unterminated object" }
        when (this[i]) {
            ',' -> {
                out.append(',')
                i++
            }

            '}' -> {
                out.newLineIndent(depth)
                out.append('}')
                return i + 1
            }

            else -> throw IllegalArgumentException("Expected ',' or '}'")
        }
    }
}

private fun String.writeJsonArray(
    start: Int,
    out: StringBuilder,
    depth: Int,
): Int {
    var i = skipWhitespace(start + 1)
    if (i < length && this[i] == ']') {
        out.append("[]")
        return i + 1
    }

    out.append('[')
    while (true) {
        out.newLineIndent(depth + 1)
        i = writeJsonValue(start = i, out = out, depth = depth + 1)
        i = skipWhitespace(i)
        require(i < length) { "Unterminated array" }
        when (this[i]) {
            ',' -> {
                out.append(',')
                i++
            }

            ']' -> {
                out.newLineIndent(depth)
                out.append(']')
                return i + 1
            }

            else -> throw IllegalArgumentException("Expected ',' or ']'")
        }
    }
}

private fun String.writeJsonString(
    start: Int,
    out: StringBuilder,
): Int {
    var i = start + 1 // opening quote
    while (i < length) {
        when (this[i]) {
            '\\' -> {
                require(i + 1 < length) { "Dangling escape" }
                i += 2
            }

            '"' -> {
                i++
                out.appendRange(this, start, i)
                return i
            }

            else -> i++
        }
    }
    throw IllegalArgumentException("Unterminated string")
}

private fun String.writeJsonPrimitive(
    start: Int,
    out: StringBuilder,
): Int {
    var i = start
    while (i < length && !this[i].isPrimitiveDelimiter()) i++
    val token = substring(start, i)
    require(token.isValidJsonPrimitive()) { "Invalid token: $token" }
    out.append(token)
    return i
}

private fun Char.isPrimitiveDelimiter(): Boolean {
    return isWhitespace() || this == ',' || this == '}' || this == ']'
}

private fun String.isValidJsonPrimitive(): Boolean =
    this == "true" || this == "false" || this == "null" || JSON_NUMBER_REGEX.matches(this)
