# console-settings-ui

The Settings addon: one **Settings** tab (always last) listing every registered `SettingsSection`, backed by a small
per-section persistence store. Depends on [`settings-api`](../settings-api/README.md) for the registration contract and
`console-io` for file access.

```kotlin
debugImplementation("io.github.thernal:console-settings-ui:<version>")
```

No code required: the addon installs itself on Android, iOS and JVM. Without it, every addon still runs on its code
defaults.

---

## What it renders

- A search field. It matches a section's own title first (showing every entry of that section when it does) and falls
  back to matching individual entry titles; `Custom` sections also match their declared keywords. Everything scrolls
  in one `LazyColumn`, with no nested scrollables.
- One row per entry kind: toggle, number field (clamps to `[min, max]`), single-choice picker (with an optional "none"
  chip) and a tag/chip editor. Rows whose `enabledWhen` is false are shown disabled.
- Two built-in sections registered by this addon:
  - **Floating widgets** (`id = "dock"`), bound to `ConsoleDock.config`: *Show floating widgets* hides every dock
    widget and overlay, and *Hidden widgets* hides individual ones by widget id (`stepper`, `inspector`…). Widgets
    that need attention, such as a paused stepper or an active inspector pick, stay visible either way.
  - **General**: *Reset all settings*, a two-tap destructive button that deletes every persisted override file and
    resets every registered section's live config to its code default (the `config.value` captured at registration
    time, before any override was applied). The UI reflects it immediately; no relaunch needed.

## Persistence

One file per section, `console-settings/<sectionId>.settings`, on `console-io`'s backup-excluded storage: a
`console-settings v1` header followed by plain `key=value` lines.

- Only values the user changed in the UI are written (sparse overrides); untouched entries keep following the code.
- Restoring is a synchronous read when a section registers, so a persisted override beats both the code default and
  whatever programmatic `updateConfig` ran before it.
- Writes run on a background dispatcher (atomic write through `ConsoleFileSystem.writeAtomically`).
- Keys the section no longer declares are dropped on the next write; a value that fails to decode, or a file with an
  unknown header, is ignored without crashing.

## Agent

With [`console-agent-ui`](../agent-ui/README.md) installed, the addon shares every registered section with an AI coding
agent (section `settings`), so no addon needs config tools of its own:

| Tool | Kind | What it does |
|------|------|--------------|
| `settings` | Tool | Each section, then one line per entry: `key = value`, title, what it takes (`true or false`, `number 1..10000`, `one of …`, `tags separated by commas`) and whether another entry disables it. `section` narrows it to one. Custom sections are listed by title only. |
| `settings_set` | Action | Sets one entry from text: `settings_set(section = "logging", key = "maxLogCount", value = "500")`. It goes through the section's `update` and `SettingsStore.persist`, like a row of the tab: the tab shows it at once and it survives a relaunch. A value out of range, an unknown option or key, and a disabled entry are refused with the reason. |
| `settings_reset` | Action | One section, or every section, back to the code defaults (the override file deleted, like *Reset all*). |

The agent's own section (`AgentConfig.SETTINGS_SECTION_ID`, `agent`) is read-only for agents: `settings_set` refuses
it and a reset without a section leaves it alone, so an agent cannot widen what it may do. Before a test run an agent
can, for example, turn on the stepper or the recomposition heatmap, and `settings_reset` afterwards.

## Architecture

```
addon.onInstall ─► SettingsRegistry.register(section)
                         │ setOnRegistered (replay + inline)
                         ▼
                   SettingsStore.restore ─► section.update { write(override) }
                         ▲
row change ─► section.update(...) + SettingsStore.persist(...) ─► <id>.settings
                         │
SettingsViewModel ◄── SettingsRegistry.sections ──► SettingsView (rows collect each section's config)
```

| Piece | Role |
|-------|------|
| `addon/SettingsAddon` | `onInstall` attaches `SettingsStore` as the registry's consumer, then registers the dock and General sections and the agent entries (`agent/SettingsAgentEntries`). Contributes `SettingsTab` (order `Int.MAX_VALUE`). |
| `store/SettingsStore` | Restores overrides on registration, persists a row change, implements *Reset all* and the agent's one-section reset. |
| `store/SettingsFileFormat` | Encodes and decodes the versioned `key=value` file. |
| `view/settings/*` | `SettingsViewModel` collects `SettingsRegistry.sections`; `SettingsContent` filters by the query and renders one row composable per `EntryKind`. |
