# console-crash-report-core

The crash-session format and the codec contract, without the addon. Depends on `console-core` only, so a module that
defines its own `Log` type can register a codec for it without pulling in any UI.

```kotlin
implementation("io.github.thernal:console-crash-report-core:<version>")
```

| Type | Role |
|------|------|
| `LogCodec<T>` | `encode` returns a log's extra fields as a `Map<String, String>`; `decode` rebuilds the log from the common `LogEnvelope` plus that map, or returns `null` to fall back to `BasicLog`. |
| `LogCodecRegistry` | `@ConsoleInternalApi`. Encoders keyed by `KClass`, decoders by the on-disk discriminator string. |
| `LogEnvelope` | The fields every log shares (id, message, level, tag, group, timestamp…). |
| `CrashSession` / `CrashSessionHeader` | One session: header (id, time, summary) and its logs, fatal one last. |
| `CrashSessionSerializer` | Hand-rolled, length-prefixed (de)serializer. `encodeStreamPrefix` + `encodeRecord` let the addon stream a file that reads back like a full session. |

Container layout: a 4-byte format version (`CRASH_FORMAT_VERSION`), the header record, then one record per log; a
record is a 4-byte big-endian length followed by its bytes. A truncated final record is tolerated, a version mismatch
discards the session, and an unknown discriminator restores that log as a `BasicLog`.

Usage and the codec example are in [`crash-report-ui`](../crash-report-ui/README.md#restoring-custom-log-types).
