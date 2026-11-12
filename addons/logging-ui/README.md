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

## Agent

With [`console-agent-ui`](../agent-ui/README.md) installed, the addon shares the logs with an AI coding agent (section
`logging`):

| Tool | Kind | What it does |
|------|------|--------------|
| `logs`, `logs_get`, `logs_wait` | Feed | Every captured log, oldest first: a filtered list, one log in full, waiting for a match. The hub keeps them, so they stay readable after the app crashed. |
| `logs_summary` | Tool | The logs kept in the app per level, kind and tag, and whether logging is on. |
| `logs_write` | Action | Writes an `AgentLog` (level `Info`, tag `agent` by default): a marker between the steps of a test, or a log of any level and tag for testing what reacts to logs. Offered only while *Allow actions* is on. |

Logs dropped while `enabled` is off are not shared either. An agent's logs have their own kind, `AgentLog`, so the
hub's `kind` filter tells them from the app's whatever their tag; in the list they look like a `BasicLog`.

## Architecture

```
Console pipeline ──emit()──► ConsoleLogObserver ──StateFlow<List<Log>>──► LogsViewModel ──► LogsView
                                   (ring buffer)  │                   └──► LogDetailViewModel ──► LogDetailView
                                                  └──SharedFlow<Log>──► agent feed `logs`
```

| Piece | Role |
|-------|------|
| `addon/LoggingAddon` | `onInstall` adds the observer to `Console`, registers `BasicLogRenderer` (for `BasicLog` and `AgentLog`), the settings section and the agent entries. Contributes `LogsTab` and `LogsNavGraph`. |
| `ConsoleLogObserver` | A `LogObserver` holding the logs as an immutable list. Drops the oldest entry once `maxLogCount` is reached; lowering the cap trims immediately. |
| `ConsoleLogObserver.stream` | Each captured log once, for the agent feed, which must tell new logs from old ones. A late subscriber gets the last 1 000 first. `emit` only calls `tryEmit` on it, so the pipeline never waits for the agent. |
| `addon/LoggingAgentEntries` | The `logging` agent entries: the `logs` feed, `logs_summary` (built by `addon/LogsSummary`) and `logs_write`, which writes an `addon/AgentLog` with `Console.notify`, so a log held by the stepper cannot stall the call. |
| `view/logs/model/LogsViewModel` | Filters by selected tags, levels and the message query. Tag chips keep the order in which a tag was first seen, so they do not jump when old logs leave the ring buffer. |
| `view/detail/model/LogDetailViewModel` | Collects the opened log plus its group, sorted by timestamp, and starts on the opened one. |
| `navigation/LogsNavGraph` | Handles `ConsoleRoute.LogDetail(logId, groupId)`. The detail page delegates to the log's renderer through `LocalLogRenderer`. |
| `addon/BasicLogRenderer` | Item and detail for `BasicLog`. Accepts optional `itemDefault` / `detailDefault` composables to replace either part. |

Logs live only in memory; for logs that survive a crash, add
[`console-crash-report-ui`](../crash-report-ui/README.md).
