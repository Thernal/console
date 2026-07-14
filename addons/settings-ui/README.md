# settings-ui

The Settings addon: a single **Settings** tab (ordered last) listing every registered
`SettingsSection`, backed by a small per-addon persistence store. Depends on `settings-api` for
the registration contract and `console-io` for file access.

```kotlin
implementation("io.github.thernal:console-settings-ui:<version>")
```

---

## What it renders

- A search field filters entry titles (`Entries` sections) or a section's title/keywords
  (`Custom` sections) — everything scrolls in one `LazyColumn`, no nested scrollables.
- One row per entry kind: toggle, number field (clamps to `[min, max]`), single-choice picker
  (with an optional "none" chip), tag/chip editor, and a plain-or-destructive action.
- A **General** section with "Reset all settings" — a two-tap destructive action that deletes
  every persisted override file. In-session values stay until the next launch: the
  programmatic-init layer that ran at startup can't be reconstructed mid-session.

## Persistence

One file per section (`console-settings/<sectionId>.settings`) on `console-io`'s backup-excluded
storage: a version header followed by plain `key=value` lines. Restoring is a synchronous read at
section-registration time, so a persisted override always beats the code default *and* whatever
programmatic init ran first. Writing happens on a background dispatcher. Keys the current entry
set no longer declares are dropped on the next write; a value that fails to decode, or a file with
an unrecognized version header, is discarded without crashing.
