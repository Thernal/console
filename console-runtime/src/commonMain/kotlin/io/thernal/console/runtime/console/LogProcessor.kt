package io.thernal.console.runtime.console

import io.thernal.console.runtime.log.Log

fun interface LogProcessor {
    fun process(log: Log): Log
}
