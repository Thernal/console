# console-settings-api

Contract module for the settings addon family: the only settings module other addons (and the host app) depend on to
register settings. No rendering and no persistence: those belong to [`settings-ui`](../settings-ui/README.md), which is
optional. Without it, registrations sit inert and the code defaults apply.

```kotlin
implementation("io.github.thernal:console-settings-api:<version>")
// release: same API, registrations are dropped
releaseImplementation("io.github.thernal:console-settings-api-noop:<version>")
```

---

## Registering entries

Bind a facade's existing `StateFlow` config, so there is no parallel state to keep in sync. Register from
`ConsoleAddon.onInstall()`:

```kotlin
@file:OptIn(ConsoleInternalApi::class)

SettingsRegistry.register(
    settingsEntries(
        id = "crash-report",              // unique; also the persistence file name
        title = "Crash Report",
        order = 30,                       // position in the Settings tab, ascending
        config = CrashReports.config,
        update = CrashReports::updateConfig,
    ) {
        toggle("showSafeSessions", "Show safe sessions",
            description = "List background and clean terminations too",
            read = { it.showSafeSessions }, write = { copy(showSafeSessions = it) })

        int("maxSessions", "Max sessions", min = 1, max = 100,
            read = { it.maxSessions }, write = { copy(maxSessions = it) })

        enum("persistLevelAtLeast", "Persist level at least", options = LogLevel.entries, noneLabel = "Any",
            enabledWhen = { it.persistOnMatch },
            read = { it.persistLevelAtLeast }, write = { copy(persistLevelAtLeast = it) })
    },
)
```

| Kind | Value | Extra parameters |
|------|-------|------------------|
| `toggle` | `Boolean` | — |
| `int` | `Int` | `min`, `max` (the field clamps to them) |
| `enum` | `E?` | `options`; `noneLabel` adds a "none" choice and makes the value nullable |
| `tags` | `Set<String>` | — |

Every kind also takes an optional `description` and `enabledWhen: (C) -> Boolean`, which greys the row out when it
returns `false` (for example, a threshold that only matters while its feature is on).

Operations ("clear all", "reset") are not settings entries: they stay in the addon tab's own `Actions()`, or in a
custom section (below).

Section `order` values used by first-party addons: Logging 10, Stepper 20, Crash Report 30, Network 40, Floating
widgets 50, Inspector 50, General last.

## Escape hatch

`settingsCustom` contributes free-form `LazyListScope` items for non-standard UI. It loses entry-level search and
persistence inside its own boundary; `keywords` widen what the search matches for the section.

```kotlin
SettingsRegistry.register(
    settingsCustom(id = "app-debug", title = "My App", keywords = setOf("environment")) {
        item { EnvironmentSwitcherRow() }
    },
)
```

## Architecture

| Type | Role |
|------|------|
| `SettingsSection` | Sealed: `Entries<C>` (typed entries bound to a config `StateFlow`, plus its `default`, the value at registration time) or `Custom` (lazy items). |
| `SettingsEntry<C, T>` | One row: `key`, `title`, `kind`, `read` / `write`, `enabledWhen` and an `EntryCodec<T>`. The key plus the codec are the persistence binding, so declaring the entry is all an addon does. |
| `EntryKind<T>` | Render metadata per kind (`Toggle`, `IntField`, `EnumPicker`, `Tags`), paired one to one with a row in `settings-ui`. |
| `SettingsRegistry` | `@ConsoleInternalApi`, mirrors `LogRendererRegistry`. Exposes `sections: StateFlow`. |

`SettingsRegistry.register` is called by every section owner. `setOnRegistered` is a single-consumer hook whose only
intended caller is `settings-ui`'s store, once, at install: it replays every section registered so far and then sees
each new one inline, so persisted values are restored exactly once whatever the addon install order.

Registering a section with a duplicate `id`, or an entry with a duplicate `key` within a section, fails fast with a
`check`: keys are the persistence contract, so a silent collision would corrupt stored overrides.

Addons register against `SettingsRegistry` directly instead of through a `ConsoleAddon` hook, which keeps
`console-api` free of a `settings-api` dependency.
