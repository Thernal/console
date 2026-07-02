# console-crash-report

Records crash sessions locally so you can inspect a crash right on the device — the fatal stack
trace plus the logs that led up to it — without waiting for a remote crash-reporting round-trip.

Two modules:

| Module | What it is |
|--------|------------|
| `console-crash-report-core` | Contract + serialization: `LogEnvelope`, `LogCodec`, `LogCodecRegistry`, the length-prefixed session format. Depends on `console-core` only. |
| `console-crash-report-ui` | The addon: streaming persistence, crash capture, termination classification, and the **Crashes** tab (session list → session detail). |

## Install

```kotlin
// build.gradle.kts
dependencies {
    debugImplementation("io.github.thernal:console-crash-report-ui:<version>")
}
```

No code required — the addon auto-initializes, streams every Console log to the current session's
file as it arrives, and installs an uncaught-exception handler (chained to any existing one).

## How it works

- **Streaming persistence.** Each log is appended to a per-session file the moment it is emitted
  (flushed to the OS per record), so the preceding logs survive even *uncatchable* terminations —
  native crashes, OOM kills, force-stops — where no handler ever runs.
- **Crash capture.** On an uncaught exception the handler appends the fatal record, writes a small
  `.crash` sidecar (summary + stack trace), finalizes the session, then chains onward so the app
  still dies normally.
- **Classification.** On the next launch every leftover session is classified from its sidecars:
  **Confirmed** (trace captured), **Probable** (died in the foreground — native/OOM/ANR), or
  **Safe** (killed in the background / clean desktop exit; hidden by default — toggleable from
  the in-console settings screen).
- **Storage.** Raw length-prefixed files in a backup-excluded location (Android
  `noBackupFilesDir`, iOS Application Support + `NSURLIsExcludedFromBackupKey`, JVM home).
  Retention keeps the newest sessions and evicts the oldest.

## Configuration

```kotlin
CrashReports.updateConfig {
    copy(
        persistOnMatch = true,                  // enable the save-filter below
        persistLevelAtLeast = LogLevel.Info,
        excludeTags = setOf("Noisy"),
        bodyPolicy = CrashBodyPolicy.Truncated, // None / Truncated / Full (default)
        maxBodySize = 4_096,
        maxSessions = 10,                       // applies from the next launch
        redactor = { log -> log },              // return null to drop a log entirely
    )
}
```

Capture follows `Console.isEnabled` — there is no separate flag. Network headers are already
masked upstream at capture (`SensitiveHeaders`); `bodyPolicy` and `redactor` are the persistence-
side levers for bodies and everything else.

## Restoring custom log types

Without any registration a custom `Log` type still round-trips as a `BasicLog` (message, level,
tag, timestamp survive; extra fields drop). For a full-fidelity restore, register a codec against
the lightweight `console-crash-report-core` — no dependency on the addon itself:

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

The discriminator string is the on-disk key — keep it stable across app versions. The first-party
`NetworkLog` codec ships with the addon, so restored network logs render with their full detail.

## Scope & limitations

- Only **managed** uncaught exceptions produce a stack trace. Native signal crashes (SIGSEGV),
  OOM kills, and force-stops cannot be caught — those sessions still appear (with their streamed
  logs) as **Probable**, just without a trace.
- Session metadata (app version, OS, device) is deliberately not captured — the reports never
  leave the user's own device.
- Intended for debug builds: exclude the addon from release (`debugImplementation`).
