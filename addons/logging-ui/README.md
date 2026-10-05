# console-logging-ui

The **Logs** tab: the log list, the log detail pager and the renderer for the built-in `BasicLog`. Without it
Console still runs its pipeline, but nothing shows the logs.

```kotlin
debugImplementation("io.github.thernal:console-logging-ui:<version>")
```

No code required: the addon installs itself on Android, iOS and JVM.

---

## What it shows

- **Log list** (newest first) with a search field and two filter rows: tags and levels. The search matches the log
  **message**; tags and levels are chip filters, and an empty selection means "all".
- **Detail pager**: opening a log shows it together with every log that shares its `groupId` (a network request and
  its response, for example), oldest first, one page each.
- **App bar actions**: copy the whole list as plain text (each log's `toShareText()`, so custom types contribute their
  own content) and clear the list.

Custom log types render in the same list once their renderer is registered (see
[`console-api`](../../console-api/README.md#logrenderer)); `BasicLog` uses `BasicLogRenderer`.

## Settings

Section **Logging** (`id = "logging"`) in the Settings tab:

| Key | Meaning | Default |
|-----|---------|---------|
| `enabled` | Capture new logs; while off, incoming logs are dropped | `true` |
| `maxLogCount` | Size of the in-memory ring buffer (1–10 000) | `1000` |

## Architecture

```
Console pipeline ──emit()──► ConsoleLogObserver ──StateFlow<List<Log>>──► LogsViewModel ──► LogsView
                                   (ring buffer)                     └──► LogDetailViewModel ──► LogDetailView
```

| Piece | Role |
|-------|------|
| `addon/LoggingAddon` | `onInstall` adds the observer to `Console`, registers `BasicLogRenderer` and the settings section. Contributes `LogsTab` and `LogsNavGraph`. |
| `ConsoleLogObserver` | A `LogObserver` holding the logs as an immutable list. Drops the oldest entry once `maxLogCount` is reached; lowering the cap trims immediately. |
| `view/logs/model/LogsViewModel` | Filters by selected tags, levels and the message query. Tag chips keep the order in which a tag was first seen, so they do not jump when old logs leave the ring buffer. |
| `view/detail/model/LogDetailViewModel` | Collects the opened log plus its group, sorted by timestamp, and starts on the opened one. |
| `navigation/LogsNavGraph` | Handles `ConsoleRoute.LogDetail(logId, groupId)`. The detail page delegates to the log's renderer through `LocalLogRenderer`. |
| `addon/BasicLogRenderer` | Item and detail for `BasicLog`. Accepts optional `itemDefault` / `detailDefault` composables to replace either part. |

Logs live only in memory; for logs that survive a crash, add
[`console-crash-report-ui`](../crash-report-ui/README.md).
