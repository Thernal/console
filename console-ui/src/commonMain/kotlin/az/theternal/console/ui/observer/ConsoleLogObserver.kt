package az.theternal.console.ui.observer

import az.theternal.console.runtime.Console
import az.theternal.console.runtime.api.LogObserver
import az.theternal.console.runtime.model.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal object ConsoleLogObserver : LogObserver {
    private const val DEFAULT_MAX_LOG_COUNT: Int = 1_000

    private val _logs = MutableStateFlow<List<Log>>(emptyList())
    val logs: StateFlow<List<Log>> = _logs.asStateFlow()
    private val _config = MutableStateFlow(Config())
    val config: StateFlow<Config> = _config.asStateFlow()

    override suspend fun emit(event: Log) {
        val configSnapshot = _config.value
        if (!configSnapshot.enabled) return
        val maxLogCount = configSnapshot.maxLogCount

        _logs.update { current ->
            when {
                maxLogCount <= 0 -> emptyList()
                current.size < maxLogCount -> current + event
                else -> current.drop(1) + event
            }
        }
    }

    fun updateConfig(config: Config) {
        val normalized = config.copy(maxLogCount = config.maxLogCount.coerceAtLeast(0))
        _config.value = normalized
        _logs.update { current ->
            when {
                normalized.maxLogCount <= 0 -> emptyList()
                current.size <= normalized.maxLogCount -> current
                else -> current.takeLast(normalized.maxLogCount)
            }
        }
    }

    fun updateConfig(block: Config.() -> Config) {
        updateConfig(_config.value.block())
    }

    fun clear() {
        _logs.value = emptyList()
    }

    fun register() {
        Console.addObserver(this)
    }

    data class Config(
        val enabled: Boolean = true,
        val maxLogCount: Int = DEFAULT_MAX_LOG_COUNT,
    )
}
