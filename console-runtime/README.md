# console-runtime

Core types and log processing pipeline. No UI dependencies — safe to use in any layer.

```kotlin
implementation("io.github.thernal:console-runtime:0.1.0")
```

---

## Log

```kotlin
// Basic log
Console.notify {
    Log(message = "Something happened", tag = "MyTag", level = LogLevel.Info)
}

// Custom log type — implement Log.Custom for addon-specific payloads
data class NetworkLog(
    override val message: String,
    override val level: LogLevel,
    // ...
) : Log.Custom
```

### LogLevel

`None` · `Verbose` · `Debug` · `Info` · `Success` · `Warning` · `Error` · `Fatal`

---

## Console

Global singleton. Thread-safe — `notify` dispatches asynchronously; `asyncNotify` suspends until observers have processed the event.

```kotlin
// Fire-and-forget
Console.notify { Log(message = "click", tag = "UI") }

// Suspending — use inside coroutines when ordering matters
Console.asyncNotify { Log(message = "response received", level = LogLevel.Success) }
```

### Enable / disable

```kotlin
Console.isEnabled = false  // drops all events silently
```

---

## LogObserver

Implement to receive every log event dispatched through `Console`:

```kotlin
object MyObserver : LogObserver {
    override suspend fun emit(event: Log) {
        // runs on a single-threaded dispatcher — no synchronization needed
    }
}

Console.addObserver(MyObserver)
Console.removeObserver(MyObserver)
```

---

## LogProcessor

Transform or filter logs before they reach observers:

```kotlin
Console.setProcessors(listOf(
    LogProcessor { log ->
        log.takeIf { it.level != LogLevel.Verbose } ?: Log(message = "[suppressed]")
    }
))
```
