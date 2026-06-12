<div align="center">

# Console

**A gesture-triggered debug overlay for Kotlin Multiplatform apps.**  
Drop it in, swipe to open, and inspect logs, HTTP traffic, and session state — without touching production code.

[![Maven Central](https://img.shields.io/maven-central/v/io.github.thernal/console-ui?label=Maven%20Central&color=4CAF50)](https://central.sonatype.com/artifact/io.github.thernal/console-ui)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-1.x-4285F4)](https://www.jetbrains.com/compose-multiplatform/)
[![Platform](https://img.shields.io/badge/Platform-Android%20%7C%20iOS%20%7C%20JVM-orange)](#)

<!-- 📸 SCREENSHOT: Full-screen shot of the Console overlay open on a phone, showing the log list with colorful level indicators. Ideal width: ~360px, centered. -->

</div>

---

## Features at a glance

| | |
|---|---|
| 📋 **Log viewer** | Filterable log list — levels, tags, full-text search |
| 🎨 **Custom log types** | Define your own `Log` subtypes with their own UI renderers |
| 🔍 **Search & filter** | Real-time full-text search across message, tag, and level |
| 🔗 **Log grouping** | Link related logs with a shared `groupId` |
| 🌐 **Network inspector** | OkHttp + Ktor interceptors with headers, body, duration, status |
| 🗂️ **Details panel** | Live key/value sidebar for session info, flags, build metadata |
| ⏸️ **Stepper** | Pause log processing and replay events one-by-one |
| 🖥️ **Custom tabs** | Add your own full screens to the Console navigation bar |
| 👆 **Custom triggers** | Swap the default swipe for any gesture — double-tap, shake, etc. |
| 🚫 **Zero prod cost** | Noop stubs with identical APIs — no UI, no overhead in release builds |

---

## Quick start

### 1. Apply the settings plugin

Adds the JetBrains Compose repository required to resolve Compose Multiplatform artifacts.

```kotlin
// settings.gradle.kts
plugins {
    id("io.github.thernal.console") version "<version>"
}
```

### 2. Add dependencies

```kotlin
// build.gradle.kts
dependencies {
    debugImplementation("io.github.thernal:console-ui:<version>")
    debugImplementation("io.github.thernal:console-logging-ui:<version>")
    releaseImplementation("io.github.thernal:console-ui-noop:<version>")
}
```

### 3. Wrap your root composable

```kotlin
@Composable
fun App() {
    ConsoleProvider {
        YourAppContent()
    }
}
```

### 4. Open the console

Swipe **↑ ↓ ← →** anywhere on screen (default gesture).

---

## Logging

### Basic logs

```kotlin
// Fire-and-forget — thread-safe, non-blocking
Console.notify {
    Log(message = "Payment initiated", tag = "Payments", level = LogLevel.Info)
}

// Suspending — preserves ordering when called sequentially inside a coroutine
Console.asyncNotify {
    Log(message = "Token refreshed", tag = "Auth", level = LogLevel.Success)
}
```

### Log levels

| Level | Typical use |
|-------|-------------|
| `None` | No level indicator |
| `Verbose` | Trace-level detail |
| `Debug` | Development information |
| `Info` | General flow events |
| `Success` | Completed operations |
| `Warning` | Recoverable issues |
| `Error` | Failures that were handled |
| `Fatal` | Unrecoverable crashes |

<!-- 📸 SCREENSHOT: Log list showing one entry per level — each with its distinct color chip/indicator. Arrange them vertically: Verbose (grey), Debug (blue), Info (teal), Success (green), Warning (amber), Error (red), Fatal (dark red). -->

### Log grouping

Logs with the same `groupId` are visually linked — useful for correlating events like a network request and its response.

```kotlin
val id = Uuid.random().toString()

Console.notify {
    Log(message = "→ POST /auth/login", tag = "Network", level = LogLevel.Debug, groupId = id)
}

// ... later ...

Console.notify {
    Log(message = "← 200 OK (143ms)", tag = "Network", level = LogLevel.Success, groupId = id)
}
```

<!-- 📸 SCREENSHOT: Log list showing two grouped entries visually connected (indented or with a vertical bar on the left), representing a request and its response from the same call. -->

---

## Custom log types

Implement `Log` to carry structured data through the pipeline.

```kotlin
data class AnalyticsLog(
    override val message: String,
    override val level: LogLevel = LogLevel.Debug,
    val eventName: String,
    val params: Map<String, Any> = emptyMap(),
    override val id: String = Uuid.random().toString(),
    override val tag: String? = "Analytics",
    override val groupId: String? = null,
    override val timestamp: Instant = Clock.System.now(),
) : Log
```

Send it like any other log:

```kotlin
Console.notify {
    AnalyticsLog(
        message = "screen_view",
        eventName = "screen_view",
        params = mapOf("screen_name" to "Checkout"),
    )
}
```

### Custom log renderer

Implement `LogRenderer` so `AnalyticsLog` entries have their own item and detail screens, then register it for the type:

```kotlin
object AnalyticsLogRenderer : LogRenderer {
    @Composable
    override fun Item(log: Log, modifier: Modifier) {
        if (log !is AnalyticsLog) return
        AnalyticsLogItem(log, modifier)
    }

    @Composable
    override fun Detail(log: Log) {
        if (log !is AnalyticsLog) return
        AnalyticsLogDetail(log)
    }
}

object AnalyticsAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
        LogRendererRegistry.register<AnalyticsLog>(AnalyticsLogRenderer)
    }
}

// Call once at startup
AnalyticsAddon.install()
```

<!-- 📸 SCREENSHOT: Log list showing a custom log entry with a visually distinct style (different background, icon, or color chip) compared to surrounding standard log entries. -->

<!-- 📸 SCREENSHOT: Custom log detail screen — full-screen view of the custom log entry with its structured fields rendered (e.g. event name, params as a key/value table). -->

---

## Search & filter

The log list filters in real time as you type. The query matches against **message**, **tag**, and **level name**.

<!-- 📸 SCREENSHOT: Log list with the search bar expanded and a query entered (e.g. "auth"), showing only matching entries with the query term highlighted. -->

---

## Triggers

### Default trigger

The built-in gesture is a swipe sequence: **↑ ↓ ← →**

```kotlin
ConsoleProvider { YourAppContent() }  // default trigger, no configuration needed
```

### Custom swipe sequence

```kotlin
ConsoleProvider(
    trigger = ConsoleTrigger.swipeSequence(Swipe.UP, Swipe.DOWN)
) {
    YourAppContent()
}
```

### Fully custom trigger

`ConsoleTrigger` is a `fun interface` — any `Modifier` extension that calls `onDetected()` qualifies:

```kotlin
// Double-tap anywhere on screen
val doubleTapTrigger = ConsoleTrigger { onDetected ->
    pointerInput(Unit) {
        detectTapGestures(onDoubleTap = { onDetected() })
    }
}

ConsoleProvider(trigger = doubleTapTrigger) {
    YourAppContent()
}
```

```kotlin
// Long-press trigger
val longPressTrigger = ConsoleTrigger { onDetected ->
    pointerInput(Unit) {
        detectTapGestures(onLongPress = { onDetected() })
    }
}
```

---

## Network inspector

Captures HTTP traffic and renders it in the log list with method, status code, URL, headers, body, and round-trip duration. Tap any entry to see the full request/response detail.

<!-- 📸 SCREENSHOT: Log list showing several network log entries — each showing method badge (GET/POST), status code chip (200 green, 401 orange, 500 red), URL snippet, and duration in ms. -->

<!-- 📸 SCREENSHOT: Network log detail screen — expanded view showing request URL, method, headers table (with masked Authorization header), request body, response status, response headers, and response body. -->

### OkHttp

```kotlin
// build.gradle.kts
dependencies {
    debugImplementation("io.github.thernal:console-network-core:<version>")
    debugImplementation("io.github.thernal:console-network-okhttp:<version>")
    debugImplementation("io.github.thernal:console-network-ui:<version>")
}
```

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(ConsoleNetworkOkHttpInterceptor())
    .build()
```

### Ktor

```kotlin
// build.gradle.kts
dependencies {
    debugImplementation("io.github.thernal:console-network-core:<version>")
    debugImplementation("io.github.thernal:console-network-ktor:<version>")
    debugImplementation("io.github.thernal:console-network-ui:<version>")
}
```

```kotlin
val client = HttpClient {
    install(ConsoleNetworkKtorPlugin)
}
```

### Sensitive headers

`Authorization`, `Cookie`, `Set-Cookie`, `X-Api-Key`, and `Proxy-Authorization` are masked with `***` by default (`SensitiveHeaders.DEFAULT`).

```kotlin
// Custom names and mask string
ConsoleNetworkOkHttpInterceptor(
    sensitiveHeaders = SensitiveHeaders(
        names = setOf("authorization", "x-session-token"),
        mask = "[redacted]",
    )
)

// Disable masking entirely
ConsoleNetworkOkHttpInterceptor(sensitiveHeaders = SensitiveHeaders.NONE)

// Same API for Ktor
HttpClient {
    install(ConsoleNetworkKtorPlugin) {
        sensitiveHeaders = SensitiveHeaders.NONE
    }
}
```

---

## Details panel

A live key/value sidebar for session info, feature flags, user context, or any ambient state — visible at a glance without scrolling through logs.

```kotlin
// build.gradle.kts
dependencies {
    debugImplementation("io.github.thernal:console-details-ui:<version>")
    releaseImplementation("io.github.thernal:console-details-core-noop:<version>")
}
```

```kotlin
// Upsert — updates in place if the key already exists
ConsoleDetails.put("User" to "alice@example.com")
ConsoleDetails.put("Environment" to "staging")
ConsoleDetails.put("Feature:NewCheckout" to "enabled")

// Remove
ConsoleDetails.remove("Environment")
```

<!-- 📸 SCREENSHOT: Console open on the Details tab showing a clean key/value list — e.g. User, Environment, App Version, Feature flags with their current values. -->

---

## Stepper

Pauses log processing and lets you replay events one-by-one — useful for stepping through complex async flows that would otherwise scroll past instantly.

```kotlin
// build.gradle.kts
dependencies {
    debugImplementation("io.github.thernal:console-stepper-ui:<version>")
}
```

No code required. Once the module is on the classpath, the stepper control appears automatically as a floating overlay inside the console. Tap **Pause** to freeze the pipeline, **Step** to advance one event at a time, and **Resume** to return to live mode.

<!-- 📸 SCREENSHOT: Console with the Stepper overlay visible — showing the Pause / Step / Resume floating controls over the log list. Ideally captured with the pipeline paused and a queued-events badge showing a count. -->

---

## Custom tabs

Add your own full-screen view to the Console navigation bar by implementing `ConsoleAddon`.

```kotlin
// 1. Define the tab
object MetricsTab : ConsoleTab {
    override val title = "Metrics"
    override val icon = Icons.Default.BarChart
    override val order = 10  // lower = further left in the nav bar

    @Composable
    override fun Content() {
        MetricsScreen()
    }
}

// 2. Expose it via an addon
object MetricsAddon : ConsoleAddon {
    override fun tab(): ConsoleTab = MetricsTab
}

// 3. Install once at app startup
MetricsAddon.install()
```

Beyond tabs, `ConsoleAddon` also supports:

- **`navGraph()`** — register a full navigation sub-graph behind your tab
- **`overlay()`** — inject a floating composable on top of the console UI

<!-- 📸 SCREENSHOT: Console open showing the bottom navigation bar with a custom tab selected (e.g. "Metrics" with a bar-chart icon) alongside the default Logs tab. The custom tab's content screen is visible. -->

---

## Modules

### Core

| Artifact | Description |
|----------|-------------|
| `io.github.thernal:console-runtime:<version>` | Core types — `Log`, `LogLevel`, `Console`, `LogObserver` |
| `io.github.thernal:console-api:<version>` | Addon contracts — `ConsoleAddon`, `ConsoleTab`, `LogRenderer`, `LogRendererRegistry` |
| `io.github.thernal:console-ui:<version>` | Compose UI shell — `ConsoleProvider`, navigation, overlay |
| `io.github.thernal:console-ui-noop:<version>` | No-op stub for production builds |

### Addons

| Artifact | Description |
|----------|-------------|
| `io.github.thernal:console-logging-ui:<version>` | Log list, log detail screen, `BasicLog` renderer |
| `io.github.thernal:console-details-ui:<version>` | Live key/value Details panel |
| `io.github.thernal:console-details-core-noop:<version>` | No-op stub for production builds |
| `io.github.thernal:console-network-core:<version>` | Shared network log types |
| `io.github.thernal:console-network-okhttp:<version>` | OkHttp interceptor |
| `io.github.thernal:console-network-ktor:<version>` | Ktor plugin |
| `io.github.thernal:console-network-ui:<version>` | Network log UI renderer |
| `io.github.thernal:console-stepper-ui:<version>` | Pause-and-step log replay |

---

## License

MIT — see [LICENSE](LICENSE).
