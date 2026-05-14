package az.theternal.console.runtime.sanitizer

import az.theternal.console.runtime.model.Log

fun interface LogSanitizer {
    fun sanitize(event: Log): Log
}
