# Addons

Optional feature modules that extend the console. Each addon is self-contained — install only what you need.

Addons are auto-discovered by Gradle; adding a directory under `addons/` with a `build.gradle.kts` is enough to include it in the build.

---

## Available addons

### Details

Displays a persistent key/value panel — useful for session info, feature flags, or any ambient state.

```kotlin
// build.gradle.kts
implementation("io.github.thernal:addons-details-compose:0.1.0")
// production
implementation("io.github.thernal:addons-details-core-noop:0.1.0")
```

```kotlin
ConsoleDetails.put("User" to "alice@example.com")
ConsoleDetails.put("Env", "staging")
ConsoleDetails.remove("Env")
```

---

### Stepper

Pause log processing and step through events one by one — useful for debugging complex async flows.

```kotlin
implementation("io.github.thernal:addons-stepper-compose:0.1.0")
```

---

### Network

Captures HTTP traffic and renders it in the log list with method, status, URL, headers, and body.

```kotlin
// OkHttp
implementation("io.github.thernal:addons-network-core:0.1.0")
implementation("io.github.thernal:addons-network-okhttp:0.1.0")
implementation("io.github.thernal:addons-network-ui:0.1.0")

// Ktor
implementation("io.github.thernal:addons-network-core:0.1.0")
implementation("io.github.thernal:addons-network-ktor:0.1.0")
implementation("io.github.thernal:addons-network-ui:0.1.0")
```

```kotlin
// OkHttp
OkHttpClient.Builder()
    .addInterceptor(ConsoleNetworkOkHttpInterceptor())
    .build()

// Ktor
HttpClient {
    install(ConsoleNetworkKtorPlugin)
}
```

`network-ui` auto-installs on Android and iOS — no manual `install()` call needed. Network logs appear in the main log list alongside other entries.

---

## Building a custom addon

See [CONTRIBUTING.md](../CONTRIBUTING.md) for the full guide. In short:

**1. Module structure**

```
addons/
  my-addon-core/          # Log.Custom subtype + state (lib.core)
  my-addon-ui/            # LogRenderer + auto-init (lib.ui)
  my-addon-core-noop/     # No-op stub for production (lib.core)
```

**2. Define a log type** (optional — skip if your addon has no custom log shape)

```kotlin
data class MyLog(
    override val message: String,
    override val level: LogLevel,
    // ...
) : Log.Custom
```

**3. Register a renderer**

```kotlin
object MyAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
        LogRendererRegistry.register<MyLog>(MyLogRenderer)
    }
}
```

`MyLog` instances sent via `Console.notify { MyLog(...) }` will then render with `MyLogRenderer` in the existing log list — no new tab required.

**4. Wire auto-init**

Android — `AndroidManifest.xml`:
```xml
<provider
    android:name=".MyAddonAutoInit"
    android:authorities="${applicationId}.my-addon-init"
    android:exported="false" />
```

iOS / native:
```kotlin
@EagerInitialization
@OptIn(ExperimentalNativeApi::class)
private val init = consoleAddonInit { MyAddon.install() }
```
