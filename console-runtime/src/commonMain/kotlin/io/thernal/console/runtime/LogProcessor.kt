package io.thernal.console.runtime

fun interface LogProcessor {
    fun process(log: Log): Log
}
