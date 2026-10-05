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

    override fun tab(): ConsoleTab = MyTab                    // optional
    override fun navGraph(): ConsoleNavGraph = MyNavGraph     // optional
    override fun dockWidget(): ConsoleDockWidget = MyWidget   // optional
    override fun overlays(): List<ConsoleOverlay> = listOf(/* ... */) // optional
    override fun contentWrapper(): ConsoleContentWrapper = { content -> MyHost(content) } // optional
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
    override val order: Int = 50  // lower = earlier; default = Int.MAX_VALUE

    @Composable
    override fun Content() { MyTabScreen() }

    @Composable
    override fun Actions() { MyTabActions() }  // optional, shown in the app bar while the tab is selected
}
```

Every tab should use a distinct `order`. First-party values, spaced by 10: Logs 0, Stepper 10, Details 20,
Crashes 30, Inspector 40, Settings `Int.MAX_VALUE` (always last).

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

---

## ConsoleDockWidget

Interactive floating controls live in one shared dock owned by `console-ui`. A widget contributes a tab inside it:

```kotlin
object MyWidget : ConsoleDockWidget {
    override val id = "my-widget"          // unique; also what users list under "Hidden widgets"
    override val title = "Mine"
    override val icon = Icons.Outlined.Star

    @Composable override fun isAvailable() = true                 // false takes no space in the dock
    @Composable override fun status() = ConsoleDockStatus.Idle    // Active shows a dot; Attention pins it and grows a pill
    @Composable override fun summary(): String? = "3 tracked"     // live line above the panel
    @Composable override fun QuickAction() { /* beside the folded dock, only while status is Attention */ }
    @Composable override fun Panel() { /* every control, built from DsChipGroup / DsActionChip */ }
}
```

Widgets are sorted by `order`, then by `title`; leave `order` unset to keep the dock neutral. Visibility is set by
`ConsoleDock.config` (`isVisible`, `hiddenWidgetIds`), which the settings addon exposes as *Floating widgets*; a widget
whose status is `Attention` always stays visible.

---

## ConsoleOverlay

Full-screen layers above the app and below the dock and the console, ordered bottom to top: `Backdrop` (draws, never
handles touches), then `Capture` (may take over touches), then the legacy `overlay()`. Within a layer, `order`
ascends. Give an overlay the `widgetId` of your dock widget and it is hidden together with that widget.

```kotlin
override fun overlays() = listOf(
    ConsoleOverlay(layer = ConsoleOverlayLayer.Backdrop, widgetId = "my-widget") { MyDrawing() },
    ConsoleOverlay(layer = ConsoleOverlayLayer.Capture, widgetId = "my-widget") { MyTouchLayer() },
)
```

Prefer `dockWidget()` and `overlays()` over the free-form `overlay()`.

---

## ConsoleContentWrapper

`@Composable (content: @Composable () -> Unit) -> Unit` applied around the **host app content** (not the console UI),
outermost first in registration order. Use it to provide `CompositionLocal`s the app content must see; the UI
inspector uses it for its slot-table access and its font scale and RTL overrides. Register it before the first
composition: a late registration changes the composition structure above the app and resets its state.
