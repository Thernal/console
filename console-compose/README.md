# console-compose

Compose UI layer for Console. Provides `ConsoleProvider` and the default log list and detail screens.

```kotlin
implementation("io.github.thernal:console-compose:<version>")
```

For production builds, replace with `console-compose-noop` to eliminate all UI at zero cost.

---

## ConsoleProvider

Wrap your root composable once:

```kotlin
@Composable
fun App() {
    ConsoleProvider {
        YourContent()
    }
}
```

### Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| `enabled` | `true` | Disables the console entirely when `false` |
| `trigger` | swipe ↑↓←→ | Gesture that opens the console |
| `logRenderer` | `DefaultLogRenderer` | Fallback renderer for `Log.Basic` |

---

## Custom trigger

```kotlin
ConsoleProvider(
    trigger = ConsoleTrigger.swipeSequence(Swipe.UP, Swipe.DOWN, Swipe.LEFT, Swipe.RIGHT)
) { }
```

---

## Custom log renderer

Override the default item or detail appearance for `Log.Basic`:

```kotlin
ConsoleProvider(
    logRenderer = defaultLogRenderer(
        item = { log, onClick -> MyLogItem(log, onClick) },
        detail = { log, onBack -> MyLogDetail(log, onBack) },
    )
) { }
```

For addon-specific types (`Log.Custom` subclasses), register in `LogRendererRegistry` instead — `DispatchLogRenderer` picks them up automatically without any change to `ConsoleProvider`.

---

## DispatchLogRenderer

Wraps the `logRenderer` parameter internally. For each log:

1. Looks up `LogRendererRegistry` by exact `KClass` — O(1).
2. If found, delegates to the registered addon renderer.
3. Otherwise falls back to the provided `logRenderer`.

Addons that call `LogRendererRegistry.register<MyLog>(MyRenderer)` in `onInstall` work automatically — no `ConsoleProvider` change needed.
