package io.thernal.console.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Runtime-mutable network capture config, read live by both `network-ktor` and `network-okhttp`
 * at capture time — unlike the per-integration, install-time-only config each of them still
 * accepts (`ConsoleNetworkKtorConfig`, `ConsoleNetworkOkHttpInterceptor`'s constructor), which
 * seed this facade once at install time but no longer gate what's actually applied per request.
 */
object NetworkConfig {
    private val _config = MutableStateFlow(Config())
    val config: StateFlow<Config> = _config.asStateFlow()

    fun updateConfig(config: Config) {
        _config.value = config
    }

    fun updateConfig(block: Config.() -> Config) {
        updateConfig(_config.value.block())
    }

    data class Config(
        val sensitiveHeaders: Set<String> = SensitiveHeaders.DEFAULT.names,
    )
}
