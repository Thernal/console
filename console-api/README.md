# console-api

Public contracts for the Console addon system — renderers, tabs, overlays, navigation, and
the `ConsoleAddon` entry point. Depends only on [`console-core`](../console-core), **not** on
`console-runtime`: the view layer is independent of the observer pipeline. For pure data
types/contracts with no Compose, see `console-core`.

```kotlin
implementation("io.github.thernal:console-api:<version>")
```

---

## ConsoleAddon

Entry point for every addon. Install it once at app startup — auto-init handles this automatically on Android (`ContentProvider`), iOS/native (`@EagerInitialization`), and JVM (`ServiceLoader`).

`onInstall()` is the data-plane hook and takes no parameters: addons that capture logs
reference the `Console` singleton (`console-runtime`) directly there; view-only addons leave
it empty. The registration APIs (`Console.addObserver`, `LogRendererRegistry.register`) are
`@ConsoleInternalApi`, so opt in at the top of the file.

```kotlin
@file:OptIn(ConsoleInternalApi::class)

object MyAddon : ConsoleAddon {
    override fun onInstall() {
        Console.addObserver(MyObserver)                  // optional — references the Console singleton directly
        LogRendererRegistry.register<MyLog>(MyRenderer)  // optional
    }

    override fun tab(): ConsoleTab = MyTab        // optional
    override fun navGraph(): ConsoleNavGraph = MyNavGraph // optional
    override fun overlay(): @Composable BoxScope.() -> Unit = { MyOverlay() } // optional
}

// Manual install — only if you skip auto-init wiring
MyAddon.install()
```

Only observer/renderer-capturing addons need a `console-runtime` dependency (for `Console`);
view-only addons depend on `console-api` / `console-core` alone.

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

Controls how a log type appears in the log list and detail screen. Register via `LogRendererRegistry` for a specific `Log` subtype.

```kotlin
object MyLogRenderer : LogRenderer {
    @Composable
    override fun Item(log: Log, modifier: Modifier) {
        if (log !is MyLog) return
        MyLogItem(log = log, modifier = modifier)
    }

    @Composable
    override fun Detail(log: Log) {
        if (log !is MyLog) return
        MyLogDetail(log = log)
    }
}
```

---

## LogRendererRegistry

Type-keyed registry — dispatches to the correct renderer in O(1) without any `canRender()` boilerplate.

```kotlin
// Register — typically in ConsoleAddon.onInstall (requires @OptIn(ConsoleInternalApi::class))
LogRendererRegistry.register<MyLog>(MyLogRenderer)

// Lookup — used internally by DispatchLogRenderer
val renderer: LogRenderer? = LogRendererRegistry.find(log)
```

`DispatchLogRenderer` in `console-ui` checks this registry automatically for every log rendered in the list and detail screens — no extra wiring needed.

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
