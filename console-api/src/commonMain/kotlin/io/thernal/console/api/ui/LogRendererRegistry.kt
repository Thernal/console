package io.thernal.console.api.ui

import io.thernal.console.runtime.log.Log
import kotlin.reflect.KClass

object LogRendererRegistry {
    private val renderers = mutableMapOf<KClass<out Log>, LogRenderer>()

    fun <T : Log> register(
        type: KClass<T>,
        renderer: LogRenderer,
    ) {
        renderers[type] = renderer
    }

    inline fun <reified T : Log> register(renderer: LogRenderer) {
        register(T::class, renderer)
    }

    fun find(log: Log): LogRenderer? {
        return renderers[log::class]
    }
}
