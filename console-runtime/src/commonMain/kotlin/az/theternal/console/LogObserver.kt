package az.theternal.console

interface LogObserver {
    suspend fun emit(event: Log)
}
