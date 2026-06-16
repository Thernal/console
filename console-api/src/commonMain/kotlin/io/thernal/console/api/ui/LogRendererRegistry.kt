package io.thernal.console.api.ui

import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.core.log.Log
import kotlin.reflect.KClass

object LogRendererRegistry {
    private val renderers = mutableMapOf<KClass<out Log>, LogRenderer>()

    @ConsoleInternalApi
    fun <T : Log> register(
        type: KClass<T>,
        renderer: LogRenderer,
    ) {
        renderers[type] = renderer
    }

    @ConsoleInternalApi
    inline fun <reified T : Log> register(renderer: LogRenderer) {
        register(T::class, renderer)
    }

    fun find(log: Log): LogRenderer? {
        return renderers[log::class]
    }
}
