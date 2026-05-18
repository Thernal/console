package az.theternal.console.runtime

interface LogObserver {
    suspend fun emit(event: Log)
}
