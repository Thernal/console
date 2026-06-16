package io.thernal.console.core

import io.thernal.console.core.log.Log

interface LogObserver {
    suspend fun emit(event: Log)
}
