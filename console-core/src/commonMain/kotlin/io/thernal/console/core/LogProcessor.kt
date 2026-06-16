package io.thernal.console.core

import io.thernal.console.core.log.Log

fun interface LogProcessor {
    fun process(log: Log): Log
}
