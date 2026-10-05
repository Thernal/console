# Addons

Optional modules that extend Console. Each is self-contained — install only what you need.

---

## Logging

Provides the log list, log detail screen, and `BasicLog` renderer. Required if you want to see logs in the console.

```kotlin
implementation("io.github.thernal:console-logging-ui:<version>")
```

No code required. Auto-installs on Android, iOS, and JVM via the addon system.

---

## Details

Displays a live key/value panel inside the console. Useful for session info, feature flags, build metadata, or any ambient state you want visible without digging through logs.

```kotlin
// debug
implementation("io.github.thernal:console-details-ui:<version>")
// release — no-op stub with the same API, no UI overhead
implementation("io.github.thernal:console-details-core-noop:<version>")
```

```kotlin
// Upsert a key/value pair — visible immediately in the Details tab
ConsoleDetails.put("User" to "alice@example.com")
ConsoleDetails.put("Env" to "staging")

// Remove a key
ConsoleDetails.remove("Env")
```

---

## Stepper

Pauses log processing and lets you replay events one by one — useful for stepping through complex async flows that would otherwise fly past.

```kotlin
implementation("io.github.thernal:console-stepper-ui:<version>")
```

No code required. The stepper control appears inside the console automatically once the module is on the classpath.

---

## UI inspector

Inspects the running Compose UI from inside the console: the composable tree, bounds, sizes and
modifiers, tap-to-select on the app, and recomposition tracking with counters, flashes and a heatmap.
Works on Android, iOS and JVM; everything is read through Compose's tooling APIs, so no per-platform
code is needed.

```kotlin
// debug
implementation("io.github.thernal:console-inspector-ui:<version>")
// release — no-op stub with the same API, no UI overhead
implementation("io.github.thernal:console-inspector-core-noop:<version>")
```

No code required: the **Inspector** tab appears and the overlay is installed automatically.
On JVM desktop, call `enableInspectorSourceInformation()` (`io.thernal.console.inspector.ui`) before creating the window, otherwise
composable names are missing for the root composition.

- **Framework components:** tapping picks the smallest component under the finger, `Text`, `Icon` and `Box` included, so
  you can measure the gap between two texts. *Details* has *Select parent* to go up to the composable that uses them.
  The *framework filter* in Settings only thins out the tree, bounds and labels. Plain `Layout` calls are named after
  the composable that uses them (`BasicText`, `Box`). Items that share an edge read `Touching · 0 dp`.
- **Padding:** the selected component's own `Modifier.padding` is drawn as an amber band with its size in dp, and the
  *Details* tab lists it (`top 18 · left 18 · right 18 · bottom 18 dp`). It is read from where each padding modifier sits in
  the chain, so stacked paddings and RTL are right. The selection box itself is the content, without its padding, but tapping the
  padding still picks that component, not its parent.
- **Tab:** *Tree* (search, collapse), *Details* (source, size in dp and px, parent, modifiers,
  parameters, recomposition count and last reason), *Recompositions* (ranking and composition timing).
  Actions: inspect, freeze, refresh, copy the tree, reset statistics.
- **Quick controls:** the inspector is in the floating dock as a tab by default; turn off *Show quick controls* in Settings to remove it.
  Its controls are icon-only buttons (long press shows the name) grouped as *Actions* (inspect, measure, freeze, tree), *Overlay* (bounds, labels, counters, heatmap, flash on recomposition,
  reset) and *Environment* (font scale, RTL). While you pick, the dock turns amber; fold it and it becomes a pill with **Details** and
  **Exit**, which stays on screen even when floating widgets are hidden. The dock never folds by itself.
- **Recomposition counts:** a scope's first pass is its initial composition and is not counted. Scopes without a
  group identity (most of them) are matched to their composable through the scope object in the slot table, so they are
  counted too. A pass where the parent recomposed but the composable's parameters had not changed is a **skip**: the
  function did not run. Skips are not counted as recompositions (they never flash or heat the heatmap) and are listed
  separately as *Skipped* in *Details*, the same split Android Studio makes. *Details* shows the composable's own count,
  and `20 own · 40 with framework below` when hidden framework components under it (a `Text`) recomposed as well; the
  heatmap and the ×N tags use the larger number so lambda-driven updates are not lost. Checked against a `SideEffect`
  counter on Android and the iOS simulator (20 recompositions counted as 20, 20 skips as 20 skips, on both). A scope
  that recomposes before the first tree read counts as its first pass. The skip flag is a Compose internal: if a Compose
  version changes it, the read switches itself off and every pass counts, so the numbers include skips again.
- **Measure:** the ruler button in the dock turns inspect mode into a two-point ruler. The first tap sets a green anchor;
  tapping or dragging over another item then draws the distance between them in dp, and the dock row repeats it
  (`↔ 24 · ↕ 12 dp`). Side by side or stacked items show the gap between their facing edges, diagonal ones an L of two
  gaps, and an item inside the other shows its four insets. Tap the anchor again to choose a new one.
- **Inspect mode:** closes the console, then tap or drag over the app to pick a component. The overlay
  shows its bounds, size and the distance to its parent; **Details** reopens the tab.
- **Settings** (Settings tab, persisted): enable, quick controls, recomposition tracking, flash highlight and duration,
  heatmap and threshold, counters, bounds, labels, measurements, framework filter and app source
  filters, auto-refresh, and font scale / layout direction overrides.

```kotlin
// Programmatic control, same API in the no-op artifact
ConsoleInspector.updateConfig { copy(showBounds = true, showRecompositionCounters = true) }
ConsoleInspector.dispatch(InspectorCommand.ResetStats)
```

Notes: composition timings only count passes that ran app composables, so the inspector's own UI never
inflates them. Names and source positions need the slot-table source information, which the addon switches
on at install time (debug only). Library composables on iOS have no parameter names. Hidden
"framework" composables are matched by a built-in list of file names; set app source filters to
define app code precisely.

---

## Network

Captures HTTP traffic and renders it in the log list with method, status code, URL, headers, body, and round-trip duration. Tap any entry to see the full request/response detail.

### OkHttp

```kotlin
implementation("io.github.thernal:console-network-core:<version>")
implementation("io.github.thernal:console-network-okhttp:<version>")
implementation("io.github.thernal:console-network-ui:<version>")
```

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(ConsoleNetworkOkHttpInterceptor())
    .build()
```

### Ktor

```kotlin
implementation("io.github.thernal:console-network-core:<version>")
implementation("io.github.thernal:console-network-ktor:<version>")
implementation("io.github.thernal:console-network-ui:<version>")
```

```kotlin
val client = HttpClient {
    install(ConsoleNetworkKtorPlugin)
}
```

### Sensitive headers

By default, `Authorization`, `Cookie`, `Set-Cookie`, `X-Api-Key`, and `Proxy-Authorization` header values are replaced with `***`. Pass a `SensitiveHeaders` instance to change this behavior:

```kotlin
// Custom set and mask string
ConsoleNetworkOkHttpInterceptor(
    sensitiveHeaders = SensitiveHeaders(
        names = setOf("authorization", "x-session-token"),
        mask = "[redacted]",
    )
)

HttpClient {
    install(ConsoleNetworkKtorPlugin) {
        sensitiveHeaders = SensitiveHeaders(
            names = setOf("authorization", "x-session-token"),
            mask = "[redacted]",
        )
    }
}
```

```kotlin
// Disable masking entirely — show all header values as-is
ConsoleNetworkOkHttpInterceptor(sensitiveHeaders = SensitiveHeaders.NONE)

HttpClient {
    install(ConsoleNetworkKtorPlugin) {
        sensitiveHeaders = SensitiveHeaders.NONE
    }
}
```

`SensitiveHeaders.DEFAULT` is the default — the 5 headers listed above, masked with `***`.

`console-network-ui` auto-installs its renderer on Android, iOS, and JVM via the addon system — no manual `install()` call needed. Network logs appear inline in the main log list alongside other entries.

---

## Building a custom addon

### Module structure

```
addons/
  my-addon-core/      # Log subtype + any state (convention.lib.core)
  my-addon-ui/        # LogRenderer + auto-init (convention.lib.ui)
  my-addon-core-noop/ # No-op stub for production (convention.lib.core)
```

### 1. Define a log type

```kotlin
data class MyLog(
    override val message: String,
    override val level: LogLevel,
    val customField: String,
    // standard Log fields with defaults ...
) : Log
```

### 2. Register a renderer

```kotlin
@file:OptIn(ConsoleInternalApi::class)

object MyAddon : ConsoleAddon {
    override fun onInstall() {
        LogRendererRegistry.register<MyLog>(MyLogRenderer)
    }
}
```

`MyLog` entries sent via `Console.notify { MyLog(...) }` will render with `MyLogRenderer` in the existing log list. No new tab required unless you want one.

### 3. Wire auto-init

**Android** — subclass `ConsoleAutoInitProvider` and declare it in `AndroidManifest.xml`:

```xml
<provider
    android:name=".MyAddonAutoInit"
    android:authorities="${applicationId}.my-addon-init"
    android:exported="false" />
```

**iOS / native** — top-level eager property:

```kotlin
@EagerInitialization
@OptIn(ExperimentalNativeApi::class)
private val init = consoleAddonInit { MyAddon.install() }
```

**JVM** — a `ConsoleInitializer` discovered via `ServiceLoader` (the JVM counterpart of the
Android `ContentProvider` and native `@EagerInitialization` hooks). Triggered once by the
console shell at first `ConsoleProvider` composition — no end-user call required.

```kotlin
// src/jvmMain/kotlin/.../MyAddonAutoInit.kt
internal class MyAddonAutoInit : ConsoleInitializer {
    override fun init() = MyAddon.install()
}
```

```
// src/jvmMain/resources/META-INF/services/io.thernal.console.api.autoinit.ConsoleInitializer
com.example.MyAddonAutoInit
```
