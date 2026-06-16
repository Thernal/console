package io.thernal.console.network

import io.thernal.console.core.log.LogLevel

private const val HTTP_REDIRECT = 300
private const val HTTP_CLIENT_ERROR = 400
private const val HTTP_SERVER_ERROR = 500

fun Int.toNetworkLevel(): LogLevel {
    return when {
        this < HTTP_REDIRECT -> LogLevel.Success
        this < HTTP_CLIENT_ERROR -> LogLevel.Info
        this < HTTP_SERVER_ERROR -> LogLevel.Warning
        else -> LogLevel.Error
    }
}
