package az.theternal.console.core

import az.theternal.console.core.base.Log
import az.theternal.console.core.base.LogObserver
import az.theternal.console.core.ConsoleLogObserver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object Console {
    var isEnabled: Boolean = true

    val logObserver = ConsoleLogObserver()
    private val observers = mutableListOf<LogObserver>(logObserver)
    private val asyncScope = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1))
    private val processMutex = Mutex()

    fun addObserver(observer: LogObserver) {
        observers.add(0, observer)
    }

    fun removeObserver(observer: LogObserver) {
        observers.remove(observer)
    }

    fun notify(event: () -> Log) {
        if (!isEnabled) return
        val e = event()
        asyncScope.launch {
            processMutex.withLock { processEvent(e) }
        }
    }

    suspend fun asyncNotify(event: () -> Log) {
        if (!isEnabled) return
        val e = event()
        processMutex.withLock { processEvent(e) }
    }

    private suspend fun processEvent(event: Log) {
        observers.forEach { observer ->
            runCatching { observer.emit(event) }
        }
    }
}
