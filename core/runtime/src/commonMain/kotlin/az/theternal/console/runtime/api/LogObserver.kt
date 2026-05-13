package az.theternal.console.runtime.api

import az.theternal.console.runtime.model.Log

interface LogObserver {
    suspend fun emit(event: Log)
}
