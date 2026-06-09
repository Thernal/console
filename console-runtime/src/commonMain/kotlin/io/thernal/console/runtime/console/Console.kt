package io.thernal.console.runtime.console

import io.thernal.console.runtime.log.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.concurrent.Volatile

class Console private constructor() : ConsoleScope {

    private val observers = MutableStateFlow<List<LogObserver>>(emptyList())
    private val processors = MutableStateFlow<List<LogProcessor>>(emptyList())
    private val asyncScope = CoroutineScope(SupervisorJob() + Dispatchers.Default.limitedParallelism(1))
    private val processMutex = Mutex()

    @Volatile
    override var isEnabled: Boolean = true

    override fun addObserver(observer: LogObserver) {
        observers.update { current ->
            if (current.any { it === observer }) {
                current
            } else {
                listOf(observer) + current
            }
        }
    }

    override fun removeObserver(observer: LogObserver) {
        observers.update { current -> current.filterNot { it === observer } }
    }

    override fun setProcessors(processors: List<LogProcessor>) {
        this.processors.value = processors
    }

    override fun notify(event: () -> Log) {
        if (!isEnabled) return
        val e = event()
        asyncScope.launch {
            processMutex.withLock { processEvent(e) }
        }
    }

    override fun blockingNotify(event: () -> Log) {
        if (!isEnabled) return
        val e = event()
        runBlocking {
            processMutex.withLock { processEvent(e) }
        }
    }

    override suspend fun asyncNotify(event: () -> Log) {
        if (!isEnabled) return
        val e = event()
        processMutex.withLock { processEvent(e) }
    }

    private suspend fun processEvent(event: Log) {
        val processed = processors.value.fold(event) { current, processor ->
            runCatching { processor.process(current) }.getOrDefault(current)
        }
        val snapshot = observers.value
        snapshot.forEach { observer ->
            runCatching { observer.emit(processed) }
        }
    }

    companion object : ConsoleScope {
        private val default = Console()

        override var isEnabled: Boolean
            get() = default.isEnabled
            set(value) {
                default.isEnabled = value
            }

        override fun addObserver(observer: LogObserver) {
            default.addObserver(observer)
        }
        override fun removeObserver(observer: LogObserver) {
            default.removeObserver(observer)
        }
        override fun setProcessors(processors: List<LogProcessor>) {
            default.setProcessors(processors)
        }
        override fun notify(event: () -> Log) {
            default.notify(event)
        }
        override fun blockingNotify(event: () -> Log) {
            default.blockingNotify(event)
        }
        override suspend fun asyncNotify(event: () -> Log) {
            default.asyncNotify(event)
        }
    }
}
