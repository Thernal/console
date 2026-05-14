package az.theternal.console.runtime.api

import az.theternal.console.runtime.model.Log
import az.theternal.console.runtime.sanitizer.LogSanitizer

interface ConsoleScope {
    var isEnabled: Boolean
    fun addObserver(observer: LogObserver)
    fun removeObserver(observer: LogObserver)
    fun setSanitizers(sanitizers: List<LogSanitizer>)
    fun notify(event: () -> Log)
    suspend fun asyncNotify(event: () -> Log)
}
