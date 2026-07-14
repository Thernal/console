# console-io

Shared file-system infrastructure — a peer of `console-core`, not an addon. No Compose, no
`console-runtime`. Extracted from crash-report's `CrashFileSystem` / `CrashAppendSink`: any addon
that needs to persist a small amount of data to a backup-excluded platform directory depends on
this instead of rolling its own expect/actual pair.

```kotlin
implementation("io.github.thernal:console-io:<version>")
```

---

## ConsoleFileSystem

`expect object` with Android / iOS / JVM actuals — backup-excluded base directory, `ensureDirectory`,
`listFileNames`, `openAppend`, `readBytes`, `writeAtomically` (temp file + atomic rename), `rename`,
`delete`, `exists`. Every consumer passes its own directory name:

```kotlin
private val dir = ConsoleFileSystem.baseDirectoryPath("console-crash-report")
```

## AppendSink

Unbuffered append handle returned by `ConsoleFileSystem.openAppend` — each `append` reaches the OS
directly (no user-space buffering, no `fsync`), so a crash-adjacent tail survives process death.

## Android Context

`ConsoleIoContextHolder.applicationContext` is filled by each addon's `ConsoleAutoInitProvider`
before first composition; `ConsoleFileSystem` reads it to resolve `noBackupFilesDir`. Stays `null`
(store disabled) when auto-init never ran.
