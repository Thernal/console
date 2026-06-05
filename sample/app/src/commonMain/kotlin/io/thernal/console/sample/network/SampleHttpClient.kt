package io.thernal.console.sample.network

import io.ktor.client.HttpClient

internal expect fun createPlatformHttpClient(): HttpClient
