# Console

A lightweight, non-intrusive debug console for Kotlin Multiplatform projects. Wrap your app with `ConsoleProvider` and get a gesture-triggered overlay that collects, filters, and displays logs — on Android, iOS, and JVM.

**Platforms:** Android · iOS · JVM

---

## Quick start

### 1. Add the dependency

```kotlin
// settings.gradle.kts — add repository
pluginManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        // ...
    }
}
dependencyResolutionManagement {
    repositories {
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        // ...
    }
}
```

```kotlin
// build.gradle.kts
dependencies {
    implementation("io.github.thernal:console-compose:0.1.0")
}
```

### 2. Wrap your app

```kotlin
@Composable
fun App() {
    ConsoleProvider {
        YourAppContent()
    }
}
```

### 3. Send logs

```kotlin
// Fire-and-forget (from any thread)
Console.notify {
    Log(message = "User signed in", tag = "Auth", level = LogLevel.Success)
}

// Suspending — preserves order
Console.asyncNotify {
    Log(message = "Response: $body", tag = "HTTP", level = LogLevel.Info)
}
```

### 4. Open the console

Swipe **up → down → left → right** anywhere on screen.

---

## Modules

| Module | Artifact | Description |
|--------|----------|-------------|
| `console-runtime` | `io.github.thernal:console-runtime` | Core types: `Log`, `LogLevel`, `Console`, `LogObserver` |
| `console-api` | `io.github.thernal:console-api` | Public contracts: `ConsoleAddon`, `LogRenderer`, `LogRendererRegistry` |
| `console-compose` | `io.github.thernal:console-compose` | Compose UI: `ConsoleProvider`, log list, detail view |
| `console-compose-noop` | `io.github.thernal:console-compose-noop` | No-op stub for production builds |

### Addons

| Module | Artifact | Description |
|--------|----------|-------------|
| `addons-details-core` | `io.github.thernal:addons-details-core` | Key/value details panel API |
| `addons-details-core-noop` | `io.github.thernal:addons-details-core-noop` | No-op stub |
| `addons-details-compose` | `io.github.thernal:addons-details-compose` | Details panel Compose UI |
| `addons-stepper-compose` | `io.github.thernal:addons-stepper-compose` | Step-through debugger |
| `addons-network-core` | `io.github.thernal:addons-network-core` | `NetworkLog` type |
| `addons-network-okhttp` | `io.github.thernal:addons-network-okhttp` | OkHttp interceptor |
| `addons-network-ktor` | `io.github.thernal:addons-network-ktor` | Ktor client plugin |
| `addons-network-ui` | `io.github.thernal:addons-network-ui` | Network log renderer |

---

## Production builds

Swap `console-compose` for `console-compose-noop` in release builds to eliminate all UI and overhead at zero cost:

```kotlin
dependencies {
    debugImplementation("io.github.thernal:console-compose:0.1.0")
    releaseImplementation("io.github.thernal:console-compose-noop:0.1.0")
}
```

---

## Custom trigger

```kotlin
ConsoleProvider(
    trigger = ConsoleTrigger.swipeSequence(Swipe.UP, Swipe.DOWN)
) {
    YourAppContent()
}
```

---

## License

MIT — see [LICENSE](LICENSE).
