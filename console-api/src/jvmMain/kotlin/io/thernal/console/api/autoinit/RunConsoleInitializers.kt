package io.thernal.console.api.autoinit

import java.util.ServiceLoader
import java.util.concurrent.atomic.AtomicBoolean

private val initialized = AtomicBoolean(false)

/**
 * Runs every registered [ConsoleInitializer] exactly once per process. Safe to call
 * repeatedly (e.g. from each `ConsoleProvider` composition) — after the first run,
 * subsequent calls return immediately. A failing initializer is isolated and does not
 * prevent the others from running.
 */
fun runConsoleInitializers() {
    if (!initialized.compareAndSet(false, true)) {
        return
    }
    ServiceLoader.load(
        ConsoleInitializer::class.java,
        ConsoleInitializer::class.java.classLoader,
    ).forEach { initializer ->
        runCatching { initializer.init() }
    }
}
