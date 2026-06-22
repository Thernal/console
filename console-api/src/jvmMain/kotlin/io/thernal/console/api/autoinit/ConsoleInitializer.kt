package io.thernal.console.api.autoinit

/**
 * JVM auto-init contract. Each addon ships an implementation registered via
 * `META-INF/services/io.thernal.console.api.autoinit.ConsoleInitializer`, so the desktop
 * ([java.util.ServiceLoader]) equivalent of the Android `ContentProvider` and native
 * `@EagerInitialization` hooks runs with no end-user configuration. Discovery is triggered
 * once by the console shell ([io.thernal.console.ui] `ConsoleProvider`).
 *
 * Implementations must have a public no-arg constructor so `ServiceLoader` can instantiate them.
 */
interface ConsoleInitializer {
    fun init()
}
