# console-core

Pure-Kotlin foundation — the log model and the contracts every other module builds on.
No Compose, no observer pipeline. Both `console-api` (view layer) and `console-runtime`
(pipeline) depend on it, which is what keeps the UI layer independent of the runtime: a
release can ship the console UI and custom tabs without pulling in the `Console` pipeline.

```kotlin
implementation("io.github.thernal:console-core:<version>")
```

---

## Log

```kotlin
// Basic log
Console.notify {
    Log(message = "Something happened", tag = "MyTag", level = LogLevel.Info)
}

// Custom log type — implement Log for addon-specific payloads
data class NetworkLog(
    override val message: String,
    override val level: LogLevel,
    // ...
) : Log
```

### LogLevel

`None` · `Verbose` · `Debug` · `Info` · `Success` · `Warning` · `Error` · `Fatal`

---

## LogObserver

Interface for receiving every log event. Implement it, then register with `Console`
(see `console-runtime`):

```kotlin
object MyObserver : LogObserver {
    override suspend fun emit(event: Log) {
        // runs on a single-threaded dispatcher — no synchronization needed
    }
}
```

## LogProcessor

`fun interface` for transforming or filtering logs before they reach observers. Registered
via `Console.setProcessors`.

## ConsoleScope

The data-plane contract that `Console` implements (`notify`, `addObserver`, …). Living here
lets addons and tests target the pipeline contract without referencing the runtime singleton.

## @ConsoleInternalApi

Opt-in marker (`@RequiresOptIn`, error level) on the registration APIs — `addObserver`,
`removeObserver`, and `LogRendererRegistry.register`. These are intended for first-party
addons, not the general public API; opting in is a deliberate, reviewable step. It is a
compile-time speed bump, not a security boundary — pair it with `Console.seal()` and release
R8 obfuscation. See `console-runtime` for the sealing details.
