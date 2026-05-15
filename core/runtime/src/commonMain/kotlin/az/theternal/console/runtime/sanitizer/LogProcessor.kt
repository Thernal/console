package az.theternal.console.runtime.sanitizer

import az.theternal.console.runtime.model.Log

fun interface LogProcessor {
    fun process(log: Log): Log
}
