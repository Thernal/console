package io.thernal.console.core

import io.thernal.console.core.log.Log

interface ConsoleScope {
    @ConsoleInternalApi
    fun addObserver(observer: LogObserver)

    @ConsoleInternalApi
    fun removeObserver(observer: LogObserver)

    fun setProcessors(processors: List<LogProcessor>)
    fun notify(event: () -> Log)
    fun blockingNotify(event: () -> Log)
    suspend fun asyncNotify(event: () -> Log)
}
