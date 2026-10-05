# console-details

A live key/value panel inside the console, for ambient state you want to see without digging through logs: the
signed-in user, the environment, feature flags, build metadata.

| Module | What it is |
|--------|------------|
| `console-details-core` | The `ConsoleDetails` facade and the `ConsoleKey` helper. No UI. |
| `console-details-ui` | The **Details** tab. Auto-installs. |
| `console-details-core-noop` | Release stub with the same write API; every call does nothing. |

## Install

```kotlin
debugImplementation("io.github.thernal:console-details-ui:<version>")
releaseImplementation("io.github.thernal:console-details-core-noop:<version>")
```

A plain module that only writes details can depend on `console-details-core` alone.

## Usage

```kotlin
ConsoleDetails.put("User", "alice@example.com")   // insert or replace
ConsoleDetails.put("Env" to "staging")
ConsoleDetails.remove("Env")
ConsoleDetails.clear()
```

For keys used in several places, declare them once with `ConsoleKey`:

```kotlin
object SessionKeys {
    val UserId = object : ConsoleKey { override val name = "User id" }
}

SessionKeys.UserId.set(user.id)    // any value, stored as toString(); null removes the key
SessionKeys.UserId.remove()
```

## Architecture

```
ConsoleDetails (MutableStateFlow<Map<String, String>>) ──flow──► DetailsViewModel ──► DetailsView (Details tab)
```

- `ConsoleDetails` is a process-wide object holding an immutable map; every write replaces it atomically, so it is
  safe from any thread.
- `details-ui` contributes `DetailsTab` (order 20) through `ConsoleDetailsAddon`. `DetailsViewModel` collects
  `ConsoleDetails.flow` and the view lists the entries.
- Auto-init: Android `ConsoleDetailsAutoInit` provider, iOS `@EagerInitialization`, JVM `ServiceLoader`.

The no-op artifact keeps `put`, `remove`, `clear` and `ConsoleKey`, but has no `flow`: code that reads the map back
belongs in debug-only source sets.
