package az.theternal.console.runtime.sanitizer

import az.theternal.console.runtime.model.Log
import az.theternal.console.runtime.model.copyWith

class PatternSanitizer(
    private val pattern: Regex,
    private val replacement: String = "***",
) : LogSanitizer {
    override fun sanitize(event: Log): Log {
        val sanitizedMessage = event.message.replace(pattern, replacement)
        val sanitizedTag = event.tag?.replace(pattern, replacement)
        if (sanitizedMessage == event.message && sanitizedTag == event.tag) return event
        return event.copyWith(message = sanitizedMessage, tag = sanitizedTag)
    }
}
