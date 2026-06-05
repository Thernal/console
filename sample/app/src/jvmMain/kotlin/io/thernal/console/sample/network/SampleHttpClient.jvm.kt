package io.thernal.console.sample.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.java.Java

internal actual fun createPlatformHttpClient(): HttpClient {
    return configureConsoleClient(client = HttpClient(engineFactory = Java))
}
