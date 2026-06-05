package io.thernal.console.runtime.console

import io.thernal.console.runtime.log.Log

interface ConsoleScope {
    var isEnabled: Boolean
    fun addObserver(observer: LogObserver)
    fun removeObserver(observer: LogObserver)
    fun setProcessors(processors: List<LogProcessor>)
    fun notify(event: () -> Log)
    suspend fun asyncNotify(event: () -> Log)
}
