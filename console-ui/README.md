# console-ui

Compose UI layer for Console. Provides `ConsoleProvider` and the core console shell.

```kotlin
implementation("io.github.thernal:console-ui:<version>")
```

For production builds, replace with `console-ui-noop` to eliminate all UI at zero cost.

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

---

## Custom trigger

```kotlin
ConsoleProvider(
    trigger = ConsoleTrigger.swipeSequence(Swipe.UP, Swipe.DOWN, Swipe.LEFT, Swipe.RIGHT)
) { }
```

---

## DispatchLogRenderer

Consulted internally for every log rendered in the list and detail screens. For each log:

1. Looks up `LogRendererRegistry` by exact `KClass` — O(1).
2. If found, delegates to the registered addon renderer.
3. Otherwise falls back to the built-in `BasicLogRenderer` provided by `console-logging-ui`.

Addons that call `LogRendererRegistry.register<MyLog>(MyRenderer)` in `onInstall` work automatically — no `ConsoleProvider` change needed.
