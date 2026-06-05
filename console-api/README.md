# console-api

Public contracts for the Console addon system. Depend on this module to build addons without pulling in any Compose UI.

```kotlin
implementation("io.github.thernal:console-api:0.1.0")
```

---

## ConsoleAddon

Entry point for every addon. Install it once at app startup — auto-init providers handle this automatically on Android and iOS.

```kotlin
object MyAddon : ConsoleAddon {
    override fun onInstall(console: ConsoleScope) {
        console.addObserver(MyObserver)          // optional
        LogRendererRegistry.register<MyLog>(MyRenderer) // optional
    }

    override fun tab(): ConsoleTab = MyTab        // optional
    override fun navGraph(): ConsoleNavGraph = MyNavGraph // optional
    override fun overlay(): @Composable BoxScope.() -> Unit = { MyOverlay() } // optional
}

// Manual install (iOS / JVM)
MyAddon.install()
```

---

## ConsoleTab

Adds a tab to the console navigation bar.

```kotlin
object MyTab : ConsoleTab {
    override val title = "My Addon"
    override val icon: ImageVector = Icons.Outlined.Star
    override val order: Int = 10  // lower = earlier; default = Int.MAX_VALUE

    @Composable
    override fun Content() { MyTabScreen() }
}
```

---

## LogRenderer

Controls how a log type appears in the log list and detail screen. Register via `LogRendererRegistry` for a specific `Log.Custom` subtype.

```kotlin
object MyLogRenderer : LogRenderer {
    @Composable
    override fun Item(log: Log, onClick: () -> Unit) {
        if (log !is MyLog) return
        MyLogItem(log = log, onClick = onClick)
    }

    @Composable
    override fun Detail(log: Log, onBack: () -> Unit) {
        if (log !is MyLog) return
        MyLogDetail(log = log, onBack = onBack)
    }
}
```

---

## LogRendererRegistry

Type-keyed registry — dispatches to the correct renderer in O(1) without any `canRender()` boilerplate.

```kotlin
// Register — typically in ConsoleAddon.onInstall
LogRendererRegistry.register<MyLog>(MyLogRenderer)

// Lookup — used internally by DispatchLogRenderer
val renderer: LogRenderer? = LogRendererRegistry.find(log)
```

`DispatchLogRenderer` in `console-compose` checks this registry automatically for every log rendered in the list and detail screens — no extra wiring needed.

---

## ConsoleNavGraph

Registers additional navigation routes inside the console.

```kotlin
object MyNavGraph : ConsoleNavGraph {
    override fun EntryProviderScope<NavKey>.routes() {
        entry<MyDetailRoute> { MyDetailScreen() }
    }
}
```
