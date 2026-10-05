# Addons

Optional modules that extend Console. Each is self-contained: install only what you need. Every addon family has its
own README with setup, settings and architecture; this page is the index plus a guide to writing your own.

| Addon | Artifacts | What it does |
|-------|-----------|--------------|
| [Logging](logging-ui/README.md) | `console-logging-ui` | The **Logs** tab: log list, filters, grouped detail pager, `BasicLog` renderer. Needed to see logs at all. |
| [Details](details-ui/README.md) | `console-details-ui`, `-details-core`, `-details-core-noop` | Live key/value panel for session info, flags, build metadata. |
| [Stepper](stepper-ui/README.md) | `console-stepper-ui` | Pauses the log pipeline and releases events one at a time. |
| [Network](network-ui/README.md) | `console-network-ui`, `-network-core`, `-network-ktor`, `-network-okhttp` | HTTP capture for Ktor and OkHttp, with header masking and body formatting. |
| [Crash reports](crash-report-ui/README.md) | `console-crash-report-ui`, `-crash-report-core` | Streams logs to disk and keeps crash sessions to inspect after relaunch. |
| [UI inspector](inspector-ui/README.md) | `console-inspector-ui`, `-inspector-core`, `-inspector-core-noop` | Composable tree, picking, measuring, padding, recomposition counts and heatmap. |
| [Settings](settings-ui/README.md) | `console-settings-ui`, [`-settings-api`](settings-api/README.md), `-settings-api-noop` | The **Settings** tab and the persisted per-addon settings every other addon registers into. |

Every `-ui` artifact installs itself on Android, iOS and JVM; no `install()` call is needed. Addons with a public
facade ship a `-noop` artifact with the same API for release builds; addons without one (stepper, network, crash
reports) belong in debug-only code.

Module READMEs: [crash-report-core](crash-report-core/README.md) ·
[details-core](details-core/README.md) · [details-core-noop](details-core-noop/README.md) ·
[inspector-core](inspector-core/README.md) · [inspector-core-noop](inspector-core-noop/README.md) ·
[network-core](network-core/README.md) · [network-ktor](network-ktor/README.md) ·
[network-okhttp](network-okhttp/README.md) · [settings-api-noop](settings-api-noop/README.md)

---

## Building a custom addon

### Module structure

```
addons/
  my-addon-core/      # Log subtype, facade and state, no UI   (convention.lib.core)
  my-addon-ui/        # ConsoleAddon, renderer, tab, auto-init  (convention.lib.ui)
  my-addon-core-noop/ # same public API, empty bodies           (convention.lib.core)
```

Addon modules are discovered automatically by `settings.gradle.kts`. Addons never depend on each other's internals:
they meet through `console-api` contracts and the registries.

### 1. Define a log type (optional)

```kotlin
data class MyLog(
    override val message: String,
    override val level: LogLevel,
    val customField: String,
    // standard Log fields with defaults ...
) : Log
```

### 2. Implement the addon

`ConsoleAddon` has one data-plane hook and several optional UI contributions; implement only what you need.

```kotlin
@file:OptIn(ConsoleInternalApi::class)

object MyAddon : ConsoleAddon {
    override fun onInstall() {
        LogRendererRegistry.register<MyLog>(MyLogRenderer)   // render MyLog in the Logs tab
        Console.addObserver(MyObserver)                      // only addons that capture logs (needs console-runtime)
        SettingsRegistry.register(mySettingsSection())       // optional, see settings-api
    }

    override fun tab(): ConsoleTab = MyTab                   // a screen in the console nav bar
    override fun navGraph(): ConsoleNavGraph = MyNavGraph    // routes behind the tab
    override fun dockWidget(): ConsoleDockWidget = MyWidget  // controls in the floating dock
    override fun overlays() = listOf(                        // full-screen layers over the app
        ConsoleOverlay(layer = ConsoleOverlayLayer.Backdrop, widgetId = MyWidget.id) { MyDrawing() },
    )
    override fun contentWrapper(): ConsoleContentWrapper =   // CompositionLocals for the app content
        { content -> MyHost(content) }
}
```

`MyLog` entries sent via `Console.notify { MyLog(...) }` render with `MyLogRenderer` in the existing log list; no new
tab is required unless you want one. The contracts are described in [`console-api`](../console-api/README.md) and the
dock in the [main README](../README.md#floating-dock-and-overlays).

### 3. Wire auto-init

**Android**: subclass `ConsoleAutoInitProvider` and declare it in the module's `AndroidManifest.xml`:

```kotlin
internal class MyAddonAutoInit : ConsoleAutoInitProvider() {
    override fun init() = MyAddon.install()
}
```

```xml
<provider
    android:name=".MyAddonAutoInit"
    android:authorities="${applicationId}.my-addon-init"
    android:exported="false" />
```

**iOS / native**: a top-level eager property in `nativeMain`:

```kotlin
@EagerInitialization
@OptIn(ExperimentalNativeApi::class)
private val init = consoleAddonInit { MyAddon.install() }
```

**JVM**: a `ConsoleInitializer` discovered via `ServiceLoader`, triggered once by the console shell at the first
`ConsoleProvider` composition; no end-user call required.

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

### 4. Document it

Give the `-ui` module a README with install, usage, settings and an architecture section, and a short README in each
sibling module that points to it. Add a row to the table above.
