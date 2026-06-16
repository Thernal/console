package io.thernal.console.runtime.console

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.Log
import io.thernal.console.core.ConsoleScope
import io.thernal.console.core.LogObserver
import io.thernal.console.core.LogProcessor
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
    var isEnabled: Boolean = true

    @Volatile
    private var sealed: Boolean = false

    val isSealed: Boolean get() = sealed

    /**
     * Permanently locks registration: after this call [addObserver] and [setProcessors]
     * become no-ops. One-way — there is no unseal. Emission ([notify] and friends) and
     * [removeObserver] keep working, so already-registered observers continue to receive
     * events and the registered set can only shrink, never grow. Call once after
     * first-party setup to block later (e.g. third-party) observer injection.
     */
    fun seal() {
        sealed = true
    }

    @ConsoleInternalApi
    override fun addObserver(observer: LogObserver) {
        if (sealed) return
        observers.update { current ->
            if (current.any { it === observer }) {
                current
            } else {
                listOf(observer) + current
            }
        }
    }

    @ConsoleInternalApi
    override fun removeObserver(observer: LogObserver) {
        observers.update { current -> current.filterNot { it === observer } }
    }

    override fun setProcessors(processors: List<LogProcessor>) {
        if (sealed) return
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

        var isEnabled: Boolean
            get() = default.isEnabled
            set(value) {
                default.isEnabled = value
            }

        val isSealed: Boolean get() = default.isSealed

        fun seal() {
            default.seal()
        }

        @ConsoleInternalApi
        override fun addObserver(observer: LogObserver) {
            default.addObserver(observer)
        }

        @ConsoleInternalApi
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
