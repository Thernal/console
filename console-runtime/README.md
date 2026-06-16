# console-runtime

The `Console` singleton and its log-processing pipeline — the data plane. Depends on
`console-core` for the log model and contracts; adds no UI. `console-api` / `console-ui` do
**not** depend on this module, so the UI layer can ship without the pipeline.

```kotlin
implementation("io.github.thernal:console-runtime:<version>")
```

The log model (`Log`, `LogLevel`) and contracts (`LogObserver`, `LogProcessor`,
`ConsoleScope`) live in [`console-core`](../console-core).

---

## Console

Global singleton. Thread-safe — `notify` dispatches asynchronously; `asyncNotify` suspends
until observers have processed the event.

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

## Observers & processors (first-party)

`addObserver` / `removeObserver` / `setProcessors` feed the pipeline. `addObserver` and
`removeObserver` are marked `@ConsoleInternalApi` — they are for addons and release sinks,
not the general public API, so callers must opt in explicitly:

```kotlin
@file:OptIn(ConsoleInternalApi::class)

Console.addObserver(MyObserver)
Console.removeObserver(MyObserver)

// Processors transform/filter before observers — a natural place for redaction
Console.setProcessors(listOf(
    LogProcessor { log ->
        log.takeIf { it.level != LogLevel.Verbose } ?: Log(message = "[suppressed]")
    },
))
```

---

## Sealing — release injection lock

After registering your first-party observers (e.g. a Crashlytics sink in a release build),
lock the pipeline so nothing can add observers afterwards — including third-party SDKs that
initialize later, or runtime injection:

```kotlin
Console.addObserver(CrashlyticsLogObserver())  // your release sink
Console.seal()                                 // one-way — addObserver/setProcessors become no-ops
```

Sealing is one-way (no unseal). Emission (`notify` and friends) and `removeObserver` keep
working, so the registered set can only shrink, never grow. It is a runtime lock that
complements the compile-time `@ConsoleInternalApi` opt-in; for release builds, also enable
R8 obfuscation. It does not stop a determined in-process attacker (who has bigger levers
anyway) — it raises the bar against casual/third-party observer injection.
