# Console

A gesture-triggered debug console for Kotlin Multiplatform. Wrap your app with `ConsoleProvider`, then call `Console.notify` from anywhere to capture logs, HTTP traffic, and session state — visible at runtime without touching any production code.

**Android · iOS · JVM**

---

## What you get

- **Log viewer** — filterable log list with level indicators, tags, and full-text search
- **Network inspector** — automatic HTTP capture via OkHttp or Ktor: method, status, URL, headers, body, duration
- **Details panel** — live key/value sidebar for session info, feature flags, or any ambient state
- **Step debugger** — pause log processing and replay events one by one
- **Zero production cost** — swap `console-compose` → `console-compose-noop` in release builds; same API, empty bodies

---

## Quick start

### 1. Add dependencies

```kotlin
// build.gradle.kts
dependencies {
    debugImplementation("io.github.thernal:console-compose:0.1.0")
    releaseImplementation("io.github.thernal:console-compose-noop:0.1.0")
}
```

### 2. Wrap your root composable

```kotlin
@Composable
fun App() {
    ConsoleProvider {
        YourAppContent()
    }
}
```

### 3. Log from anywhere

```kotlin
// Fire-and-forget — thread-safe, non-blocking
Console.notify {
    Log(message = "User signed in", tag = "Auth", level = LogLevel.Success)
}

// Suspending — preserves ordering when called sequentially inside a coroutine
Console.asyncNotify {
    Log(message = "Response: $body", tag = "HTTP", level = LogLevel.Info)
}
```

### 4. Open the console

Swipe **↑ → ↓ → ← → →** anywhere on screen (default gesture).

---

## Modules

| Module | Artifact | Description |
|--------|----------|-------------|
| `console-runtime` | `io.github.thernal:console-runtime:0.1.0` | Core types: `Log`, `LogLevel`, `Console`, `LogObserver` |
| `console-api` | `io.github.thernal:console-api:0.1.0` | Addon contracts: `ConsoleAddon`, `LogRenderer`, `LogRendererRegistry` |
| `console-compose` | `io.github.thernal:console-compose:0.1.0` | Compose UI: `ConsoleProvider`, log list, detail screen |
| `console-compose-noop` | `io.github.thernal:console-compose-noop:0.1.0` | No-op stub for production builds |

Optional feature modules → [addons/README.md](addons/README.md)

---

## Addons at a glance

| Addon | Artifact(s) | What it adds |
|-------|-------------|--------------|
| **Details** | `addons-details-compose` / `addons-details-core-noop` | Live key/value panel |
| **Network** | `addons-network-core` + `addons-network-ktor` or `addons-network-okhttp` + `addons-network-compose` | HTTP inspector |
| **Stepper** | `addons-stepper-compose` | Pause-and-step log replay |

Full setup and configuration → [addons/README.md](addons/README.md)

---

## Customization

### Custom gesture trigger

```kotlin
ConsoleProvider(
    trigger = ConsoleTrigger.swipeSequence(Swipe.UP, Swipe.DOWN)
) {
    YourAppContent()
}
```

### Custom log renderer

Override how `Log.Basic` entries look in the list and detail screen:

```kotlin
ConsoleProvider(
    logRenderer = defaultLogRenderer(
        item = { log, onClick -> MyLogItem(log, onClick) },
        detail = { log, onBack -> MyLogDetail(log, onBack) },
    )
) {
    YourAppContent()
}
```

For addon log types (`Log.Custom` subclasses), register via `LogRendererRegistry` instead — no `ConsoleProvider` change needed.

---

## License

MIT — see [LICENSE](LICENSE).
