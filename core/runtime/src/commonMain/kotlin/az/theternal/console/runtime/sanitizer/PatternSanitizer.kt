package az.theternal.console.runtime.sanitizer

import az.theternal.console.runtime.model.Log

class PatternSanitizer(
    private val pattern: Regex,
    private val replacement: String = "***",
) : LogProcessor {
    override fun process(log: Log): Log {
        val sanitizedMessage = log.message.replace(pattern, replacement)
        val sanitizedTag = log.tag?.replace(pattern, replacement)
        if (sanitizedMessage == log.message && sanitizedTag == log.tag) return log
        return log.copyWith(message = sanitizedMessage, tag = sanitizedTag)
    }
}
