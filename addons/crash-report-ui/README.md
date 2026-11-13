# console-crash-report

Records crash sessions locally so you can inspect a crash right on the device (the fatal stack trace plus the logs
that led up to it) without waiting for a remote crash-reporting round-trip.

| Module | What it is |
|--------|------------|
| `console-crash-report-core` | Contract and serialization: `LogEnvelope`, `LogCodec`, `LogCodecRegistry`, `CrashSessionSerializer` and the length-prefixed session format. Depends on `console-core` only. |
| `console-crash-report-ui` | The addon: streaming persistence, crash capture, termination classification, the `CrashReports` facade and the **Crashes** tab (session list → session detail → log detail). |

## Install

```kotlin
debugImplementation("io.github.thernal:console-crash-report-ui:<version>")
```

No code required: the addon auto-initializes, streams every Console log to the current session's file as it arrives,
and installs an uncaught-exception handler (chained to any existing one). There is no no-op artifact, so keep
`CrashReports` calls in debug-only code.

## Using it

- **Crashes tab** (order 30): past sessions, newest first, each badged **Confirmed**, **Probable** or **Safe** (see
  below). Swipe a session to delete it; the app bar clears them all. The session that is recording right now is never
  listed.
- **Session detail**: the session's logs, with the fatal stack trace as the last, `Fatal`-level entry (tag `Crash`).
  Each log opens its own detail screen; network logs keep their full request/response detail. The share button exports
  the summary, the trace and the preceding logs as plain text.

```kotlin
CrashReports.sessions          // StateFlow<List<CrashSessionSummary>>, refreshed by refreshSessions()
CrashReports.open(id)          // CrashSession? : header + every log
CrashReports.share(id)         // String? : plain-text export
CrashReports.delete(id)
CrashReports.clearAll()        // the active session survives
```

## Agent

With [`console-agent-ui`](../agent-ui/README.md) installed, the addon shares the saved sessions with an AI coding agent
(section `crash-report`). Sessions are on the device, so an agent reads a crash on the next launch even when no hub
was running at the time.

| Tool | Kind | What it does |
|------|------|--------------|
| `crashes`, `crashes_get`, `crashes_wait` | Feed | One record when the app starts after its previous run crashed (`Fatal` with a trace, `Error` without one), with the session's text as detail. |
| `crash_sessions` | Tool | Saved sessions, newest first: id, how it ended, start and crash time, the exception. `crashed_only` (default on) leaves out the ones that ended without a crash; `limit` (default 10). |
| `crash_session` | Tool | One session: how it ended, the stack trace (first 80 lines) and the last `logs` (default 30) before the end, one line each or in full with `full_logs`. |
| `crash_delete`, `crash_clear` | Action | Delete one session, or all of them; the running session stays. Offered only while *Allow actions* is on. |

A loop an agent can run: the app crashes during a test, it relaunches, `crashes_wait` (or `crash_sessions`) shows
the crash, `crash_session` gives the trace with source lines; after the fix the next run leaves no crashed session.

## How it works

- **Streaming persistence.** Each log is appended to a per-session file the moment it is emitted (flushed to the OS
  per record), so the preceding logs survive even *uncatchable* terminations (native crashes, OOM kills, force-stops)
  where no handler ever runs.
- **Crash capture.** On an uncaught exception the handler appends the fatal record, writes a small `.crash` sidecar
  (summary + stack trace), finalizes the session, then chains onward so the app still dies normally.
- **Classification.** On the next launch every leftover session is classified from its sidecars: **Confirmed** (trace
  captured), **Probable** (died in the foreground: native crash, OOM, ANR), or **Safe** (killed in the background, or a
  clean desktop exit; hidden by default).
- **Storage.** Raw length-prefixed files in a backup-excluded location (Android `noBackupFilesDir`, iOS Application
  Support + `NSURLIsExcludedFromBackupKey`, JVM home). Retention keeps the newest sessions and evicts the oldest.

### Architecture

```
Console pipeline ─► CrashStreamWriter (filter → redactor → body policy) ─► CrashStore.appendLog ─► <stem>.log
uncaught exception ─► CrashCapture.onUncaught ─► fatal record + <stem>.crash + close ─► previous handler
lifecycle change ─► installLifecycleTracking ─► <stem>.state
next launch ─► CrashReports.refreshSessions ─► CrashStore.entries + classify ─► Crashes tab
```

| Piece | Role |
|-------|------|
| `addon/CrashReportAddon` | `onInstall` (guarded against a second install) starts the runtime, adds the writer to `Console`, registers the network codecs, installs the crash handler and lifecycle tracking, registers the settings section and the agent entries. Contributes `CrashReportTab` and `CrashReportNavGraph`. |
| `agent/CrashAgentEntries` | The `crash-report` agent entries, on top of `CrashReports`: the `crashes` feed (one record at startup when the newest saved session crashed), the session tools and the delete actions; text built by `CrashAgentText`. |
| `runtime/CrashReportRuntime` | Owns the process-wide `CrashStore` and opens a fresh session at start. Without a platform directory the store stays `null` and everything no-ops. |
| `writer/CrashStreamWriter` | The `LogObserver`. Applies the save filter, the `redactor` and the network body policy, then appends. |
| `store/CrashStore` | File layout, append, segment rotation, sidecars, retention, delete. |
| `handler/CrashHandler` (`expect`) | Android/JVM `Thread.setDefaultUncaughtExceptionHandler`, native `setUnhandledExceptionHook`, always chained. |
| `handler/CrashCapture` | Crash-time work, kept minimal; reentrancy-guarded, and skipped while `Console.isEnabled` is false. |
| `lifecycle/LifecycleTracking` (`expect`) | Android activity callbacks, iOS foreground/background notifications, JVM shutdown hook (`CLEAN`). Writes only on transitions. |
| `session/*` | `CrashSessionClass` and the classification rule, sidecar parsing, the summary shown in the list. |
| `codec/*` | `LogCodec`s for `NetworkLog.Request` / `Response`. |

File layout per session, in `console-crash-report/`:

| File | Content |
|------|---------|
| `<startedAtMs>__<id>.log` | Format version, header record, then one length-prefixed record per log. |
| `<stem>.log.old` | The previous segment. A segment rotates every 500 records, so a bounded window of the newest logs (the crash-adjacent part) is kept. |
| `<stem>.crash` | Summary and stack trace, written atomically only on a captured crash. |
| `<stem>.state` | Last lifecycle state (`FOREGROUND`, `BACKGROUND`, `CLEAN`). Missing means foreground. |

A torn final record (the process died mid-write) is tolerated on read; a missing or different format version
discards the session.

## Configuration

```kotlin
CrashReports.updateConfig {
    copy(
        persistOnMatch = true,                  // enable the save filter below
        persistLevelAtLeast = LogLevel.Info,
        excludeTags = setOf("Noisy"),
        bodyPolicy = CrashBodyPolicy.Truncated, // None / Truncated / Full (default)
        maxBodySize = 4_096,
        maxSessions = 10,                       // applies from the next launch
        redactor = { log -> log },              // return null to drop a log entirely
    )
}
```

Section **Crash Report** (`id = "crash-report"`) in the Settings tab exposes the same values except `redactor`:

| Key | Meaning | Default |
|-----|---------|---------|
| `showSafeSessions` | List Safe sessions too | `false` |
| `persistOnMatch` | Persist only logs matching the criteria below; otherwise every log | `false` |
| `persistLevelAtLeast` | Minimum level (`None`-level logs never match) | any |
| `includeTags` / `excludeTags` | Tag allow and deny lists | empty |
| `bodyPolicy` | How much of a network body reaches disk | `Full` |
| `maxBodySize` | Character cap for `Truncated` (256–65 536) | `4096` |
| `maxSessions` | Sessions kept on disk (1–100) | `10` |

Capture follows `Console.isEnabled`; there is no separate flag. Network headers are already masked upstream at
capture (`SensitiveHeaders`); `bodyPolicy` and `redactor` are the persistence-side levers for bodies and everything
else.

## Restoring custom log types

Without any registration a custom `Log` type still round-trips as a `BasicLog` (message, level, tag and timestamp
survive; extra fields drop). For a full-fidelity restore, register a codec against the lightweight
`console-crash-report-core`, with no dependency on the addon itself:

```kotlin
object FooLogCodec : LogCodec<FooLog> {
    override fun encode(log: FooLog): Map<String, String> = mapOf("orderId" to log.orderId)

    override fun decode(envelope: LogEnvelope, payload: Map<String, String>): FooLog? {
        val orderId = payload["orderId"] ?: return null // null → BasicLog fallback
        return FooLog(orderId = orderId, /* common fields from envelope */)
    }
}

@OptIn(ConsoleInternalApi::class)
LogCodecRegistry.register(FooLog::class, discriminator = "acme.foo", codec = FooLogCodec)
```

The discriminator string is the on-disk key: keep it stable across app versions. The first-party `NetworkLog` codecs
ship with the addon, so restored network logs render with their full detail.

## Scope & limitations

- Only **managed** uncaught exceptions produce a stack trace. Native signal crashes (SIGSEGV), OOM kills and
  force-stops cannot be caught: those sessions still appear (with their streamed logs) as **Probable**, just without a
  trace.
- Session metadata (app version, OS, device) is deliberately not captured: the reports never leave the device.
- Intended for debug builds: exclude the addon from release (`debugImplementation`).
