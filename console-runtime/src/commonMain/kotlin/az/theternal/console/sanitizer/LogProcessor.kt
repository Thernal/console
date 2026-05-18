package az.theternal.console.sanitizer

import az.theternal.console.model.Log

fun interface LogProcessor {
    fun process(log: Log): Log
}
