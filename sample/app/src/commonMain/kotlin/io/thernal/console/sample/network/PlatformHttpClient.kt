package io.thernal.console.sample.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.http.ContentType
import io.thernal.console.network.ktor.ConsoleNetworkKtorPlugin

internal fun configureConsoleClient(client: HttpClient): HttpClient {
    return client.config {
        install(ConsoleNetworkKtorPlugin) {
        }
        install(DefaultRequest) {
            headers.append(
                name = "Accept",
                value = ContentType.Application.Json.toString(),
            )
        }
    }
}
