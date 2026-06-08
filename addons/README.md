# Addons

Optional modules that extend Console. Each is self-contained — install only what you need.

---

## Details

Displays a live key/value panel inside the console. Useful for session info, feature flags, build metadata, or any ambient state you want visible without digging through logs.

```kotlin
// debug
implementation("io.github.thernal:details-compose:0.1.0")
// release — no-op stub with the same API, no UI overhead
implementation("io.github.thernal:details-core-noop:0.1.0")
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
implementation("io.github.thernal:stepper-compose:0.1.0")
```

No code required. The stepper control appears inside the console automatically once the module is on the classpath.

---

## Network

Captures HTTP traffic and renders it in the log list with method, status code, URL, headers, body, and round-trip duration. Tap any entry to see the full request/response detail.

### OkHttp

```kotlin
implementation("io.github.thernal:network-core:0.1.0")
implementation("io.github.thernal:network-okhttp:0.1.0")
implementation("io.github.thernal:network-compose:0.1.0")
```

```kotlin
val client = OkHttpClient.Builder()
    .addInterceptor(ConsoleNetworkOkHttpInterceptor())
    .build()
```

### Ktor

```kotlin
implementation("io.github.thernal:network-core:0.1.0")
implementation("io.github.thernal:network-ktor:0.1.0")
implementation("io.github.thernal:network-compose:0.1.0")
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

`addons-network-compose` auto-installs its renderer on Android and iOS via the addon system — no manual `install()` call needed. Network logs appear inline in the main log list alongside other entries.

---

## Building a custom addon

### Module structure

```
addons/
  my-addon-core/     # Log.Custom subtype + any state (convention.lib.core)
  my-addon-compose/  # LogRenderer + auto-init (convention.lib.ui)
  my-addon-core-noop/ # No-op stub for production (convention.lib.core)
```

### 1. Define a log type

```kotlin
data class MyLog(
    override val message: String,
    override val level: LogLevel,
    val customField: String,
    // standard Log fields with defaults ...
) : Log.Custom
```

### 2. Register a renderer

```kotlin
object MyAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
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
