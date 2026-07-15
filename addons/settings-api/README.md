# settings-api

Contract module for the settings addon family — the only module other addons (and the host app)
depend on to register settings. No rendering, no persistence: those belong to `settings-ui`,
which is optional. Without it, registrations sit inert and defaults win.

```kotlin
implementation("io.github.thernal:console-settings-api:<version>")
```

---

## Registering entries

Bind a facade's existing `StateFlow` config — no parallel state, no sync problem:

```kotlin
override fun settings() = settingsEntries(
    id = "crash-report",
    title = "Crash Report",
    order = 30,
    config = CrashReports.config,
    update = CrashReports::updateConfig,
) {
    toggle("showSafeSessions", "Show safe sessions",
        read = { it.showSafeSessions }, write = { copy(showSafeSessions = it) })

    int("maxSessions", "Max sessions", min = 1, max = 100,
        read = { it.maxSessions }, write = { copy(maxSessions = it) })
}
```

Available entry kinds: `toggle`, `int` (min/max), `enum` (optional `noneLabel` for a nullable
value), `tags`. Operations (e.g. "clear all") aren't settings entries — they stay on the addon
tab's own `Actions()`, or on a dedicated component inside a `settingsCustom` section (see below).

## Escape hatch

`settingsCustom` contributes free-form `LazyListScope` items for non-standard UI; it loses
entry-level search/persistence within its own boundary.

```kotlin
SettingsRegistry.register(
    settingsCustom(id = "app-debug", title = "My App", keywords = setOf("environment")) {
        item { EnvironmentSwitcherRow() }
    },
)
```

## SettingsRegistry

`@ConsoleInternalApi`, mirrors `LogRendererRegistry`. `register` is called by every section owner;
`setOnRegistered` is a single-consumer hook — its only intended caller is `settings-ui`'s store,
once, at install — which replays every already-registered section so restore runs exactly once
regardless of addon install order. Passive observers collect the public `sections: StateFlow`.

Registering a section with a duplicate `id`, or an entry with a duplicate `key` within a section,
fails fast with a `check` — keys are the persistence contract, so a silent collision would corrupt
overrides.
