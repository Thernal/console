package az.theternal.console.api

import az.theternal.console.model.Log
import az.theternal.console.sanitizer.LogProcessor

interface ConsoleScope {
    var isEnabled: Boolean
    fun addObserver(observer: LogObserver)
    fun removeObserver(observer: LogObserver)
    fun setProcessors(processors: List<LogProcessor>)
    fun notify(event: () -> Log)
    suspend fun asyncNotify(event: () -> Log)
}
