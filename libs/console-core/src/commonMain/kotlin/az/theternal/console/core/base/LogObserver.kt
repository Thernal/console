package az.theternal.console.core.base

interface LogObserver {
    suspend fun emit(event: Log)
}
