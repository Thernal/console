package io.thernal.console.runtime

interface LogObserver {
    suspend fun emit(event: Log)
}
