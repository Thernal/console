package az.theternal.console.runtime.api

import az.theternal.console.runtime.model.Log
import az.theternal.console.runtime.sanitizer.LogProcessor

interface ConsoleScope {
    var isEnabled: Boolean
    fun addObserver(observer: LogObserver)
    fun removeObserver(observer: LogObserver)
    fun setProcessors(processors: List<LogProcessor>)
    fun notify(event: () -> Log)
    suspend fun asyncNotify(event: () -> Log)
}
