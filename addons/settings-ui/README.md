# settings-ui

The Settings addon: a single **Settings** tab (ordered last) listing every registered
`SettingsSection`, backed by a small per-addon persistence store. Depends on `settings-api` for
the registration contract and `console-io` for file access.

```kotlin
implementation("io.github.thernal:console-settings-ui:<version>")
```

---

## What it renders

- A search field matches a section's own title first (showing every entry in that section when
  it does) and falls back to matching individual entry titles otherwise; `Custom` sections also
  match their declared keywords — everything scrolls in one `LazyColumn`, no nested scrollables.
- One row per entry kind: toggle, number field (clamps to `[min, max]`), single-choice picker
  (with an optional "none" chip), and a tag/chip editor.
- A **General** section with "Reset all settings" — a two-tap destructive button (`DsButton`)
  that deletes every persisted override file and resets every registered section's live config
  back to its code default (the `config.value` captured at registration time, before any override
  applied), so the UI reflects the reset immediately — no relaunch needed.

## Persistence

One file per section (`console-settings/<sectionId>.settings`) on `console-io`'s backup-excluded
storage: a version header followed by plain `key=value` lines. Restoring is a synchronous read at
section-registration time, so a persisted override always beats the code default *and* whatever
programmatic init ran first. Writing happens on a background dispatcher. Keys the current entry
set no longer declares are dropped on the next write; a value that fails to decode, or a file with
an unrecognized version header, is discarded without crashing.
