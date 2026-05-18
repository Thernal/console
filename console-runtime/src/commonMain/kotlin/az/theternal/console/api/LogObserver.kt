package az.theternal.console.api

import az.theternal.console.model.Log

interface LogObserver {
    suspend fun emit(event: Log)
}
