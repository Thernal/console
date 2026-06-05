package io.thernal.console.runtime.console

import io.thernal.console.runtime.log.Log

interface LogObserver {
    suspend fun emit(event: Log)
}
