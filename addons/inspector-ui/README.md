# console-inspector

Inspects the running Compose UI from inside the console: the composable tree, bounds, sizes, padding and modifiers,
design-tool style picking and measuring on the app, and recomposition tracking with counters, flashes and a heatmap.
Works on Android, iOS and JVM; everything is read through Compose's tooling APIs, so there is no per-platform code.

| Module | What it is |
|--------|------------|
| `console-inspector-core` | The `ConsoleInspector` facade, `InspectorConfig`, `InspectorCommand`, `InspectorFontScale`, `InspectorLayoutDirection`. No Compose. |
| `console-inspector-ui` | The addon: engine, overlay, **Inspector** tab, dock widget and settings section. Auto-installs. |
| `console-inspector-core-noop` | Release stub: the same facade, nothing stored or executed. |

## Install

```kotlin
debugImplementation("io.github.thernal:console-inspector-ui:<version>")
releaseImplementation("io.github.thernal:console-inspector-core-noop:<version>")
```

No code required: the **Inspector** tab appears and the overlay is installed automatically. On JVM desktop, call
`enableInspectorSourceInformation()` (`io.thernal.console.inspector.ui`) before creating the window, otherwise
composable names are missing for the root composition.

```kotlin
// Programmatic control, same API in the no-op artifact
ConsoleInspector.updateConfig { copy(showBounds = true, showRecompositionCounters = true) }
ConsoleInspector.dispatch(InspectorCommand.ResetStats)   // also Refresh, ClearSelection
```

---

## Features

- **Inspect mode** closes the console; tap or drag over the app to pick a component. The overlay shows its frame,
  size, padding and margin and the distance to its parent; **Details** reopens the tab.
- **Picking, step by step.** Like a design tool, a tap picks the outermost component under the finger (screen-filling
  roots and same-sized wrappers are skipped), and tapping inside the selection again steps one level in, down to
  `Text`, `Icon` and `Box`. Tapping beside the selection picks on its level. Dragging picks the smallest component
  under the finger right away. A mouse picks by clicking; hovering picks nothing. *Details* has *Select parent* to go
  back up.
- **Framework components.** Picking reaches every component; with the *framework filter* on, the steps skip hidden
  framework wrappers but still end on the `Text` or `Icon` under the finger. The filter only thins out the tree,
  bounds and labels. Plain `Layout` calls are named after the composable that uses them (`BasicText`, `Box`).
- **Frame, padding and margin.** A component's box is its frame, the edge you see: the first background, border,
  shadow or clip in its modifier chain. Padding after that is *inside* the frame and padding before it is margin, so
  `Modifier.background(...).padding(16.dp)` is framed at the background and gaps between cards are measured border to
  border. With nothing drawn, every padding counts as inside. The selected component's padding is drawn as an amber
  band inside its frame and its margin as a fainter one outside, with sizes in dp, and *Details* lists both
  (`top 18 · left 18 · right 18 · bottom 18 dp`). They are read from where each padding modifier sits in the chain, so
  stacked paddings and RTL are right. Tapping a margin still picks that component, not its parent.
- **Measure.** The ruler in the dock turns inspect mode into a two-point ruler. The first tap sets a green anchor and
  tapping inside it steps the anchor in; tapping another item then picks the second one on the anchor's level (tap
  again to step in, or drag) and draws the distance in dp, repeated in the dock row (`↔ 24 · ↕ 12 dp`). Side by side
  or stacked items show the gap between their facing edges, diagonal ones an L of two gaps, and an item inside the
  other its four insets. Items that share an edge read `Touching · 0 dp`. Turn the ruler off and on to choose a new
  anchor. In the tab, *Measure from here* in *Details* makes the selected composable the anchor; select another one in
  the tree and *Details* shows the distance (the overlay draws it too). *Clear anchor* removes it.
- **Tab** (order 40): *Tree* (search, collapse), *Details* (source, size in dp and px, parent, padding, margin,
  modifiers, parameters, recomposition count and last reason, distance from the anchor) and *Recompositions*
  (ranking, composition timing, reset). Actions: inspect, freeze, refresh, copy the tree as text.
- **Dock widget** (`id = "inspector"`): icon-only chips (long press shows the name) grouped as *Actions* (inspect,
  measure, freeze, tree), *Overlay* (bounds, labels, counters, heatmap, flash on recomposition, reset) and
  *Environment* (font scale cycle, RTL). While you pick, the dock turns amber; folded, it shows a pill with two
  buttons, open *Details* and exit, that stays on screen even when floating widgets are hidden. Turn off *Show quick controls*
  to remove the widget when not picking.
- **Environment overrides**: font scale (×0.85 to ×2 on top of the system scale) and layout direction, applied to the
  app content only.

### Recomposition counts

A scope's first pass is its initial composition and is not counted. Scopes without a group identity (most of them)
are matched to their composable through the scope object in the slot table, so they are counted too.

A pass where the parent recomposed but the composable's parameters had not changed is a **skip**: the function did not
run. Skips are not recompositions (they never flash or heat the heatmap) and are listed separately as *Skipped* in
*Details*, the same split Android Studio makes. *Details* shows the composable's own count, and
`20 own · 40 with framework below` when hidden framework components under it (a `Text`) recomposed as well; the
heatmap and the ×N tags use the larger number so lambda-driven updates are not lost. Checked against a `SideEffect`
counter on Android and the iOS simulator (20 recompositions counted as 20, 20 skips as 20 skips, on both).

A scope that recomposes before the first tree read counts as its first pass. The skip flag is a Compose internal: if
a Compose version changes it, the read switches itself off and every pass counts, so the numbers include skips again.
Composition timings only count passes that ran app composables, so the inspector's own UI never inflates them.

## Settings

Section **Inspector** (`id = "inspector"`), bound to `ConsoleInspector.config`:

| Key | Meaning | Default |
|-----|---------|---------|
| `enabled` | The whole inspector; the app UI is unaffected either way | `true` |
| `showQuickControls` | Keep the inspector in the floating dock | `true` |
| `trackRecompositions` | Count recompositions and record their reason | `true` |
| `highlightRecompositions` / `highlightDurationMillis` | Flash a composable when it recomposes, for 100–5000 ms | `true` / `600` |
| `showHeatmap` / `heatmapThreshold` | Tint by count, blue to red; the count that is fully hot (1–1000) | `false` / `30` |
| `showRecompositionCounters` | ×N tags | `false` |
| `showBounds` / `showLabels` | Frames and names of every component | `false` |
| `showMeasurements` | Distances to the parent around the selection | `true` |
| `hideFrameworkNodes` | Framework filter for the tree, overlay and ranking | `true` |
| `appSourceTags` | File or function name fragments that mark app code; everything else counts as framework | empty |
| `autoRefreshSeconds` | Rebuild the tree periodically (0–60, 0 = manual) | `0` |
| `fontScale` / `layoutDirection` | Environment overrides | follow system / app |

Without `appSourceTags`, framework composables are recognised by a built-in list of Compose file names and by
lower-case names (`remember*`, `collectAsState`). Library composables on iOS have no parameter names.

## Architecture

```
                     ┌──────────── ConsoleInspector (config StateFlow, commands) ◄── settings, dock, your code
                     ▼
App content ─► InspectorHost ─ LocalInspectionTables ─► slot tables ─► TreeBuilder ─► InspectorSnapshot
                 (density, RTL)                                                         │
Recomposer ─► RecompositionObserver ─Channel─► InspectorEngine (main thread, StateFlows) ◄┘
                                                   │            ▲
             ┌─────────────────────────────────────┼────────────┼──────────────────┐
             ▼                                     ▼            │                  ▼
   InspectorDrawLayer (Backdrop)       InspectorViewModel (tab) │      InspectorDockWidget
                                                                │
   InspectorPickLayer (Capture) ──► InspectorPicker ── PickChain / InspectorMeasure
```

The addon plugs into Console through four `ConsoleAddon` hooks (`addon/InspectorAddon`): a **content wrapper**
(`InspectorHost`), two **layered overlays** (`Backdrop` draws, `Capture` takes touches; both follow the dock widget's
visibility), the **tab** and the **dock widget**. `onInstall` switches on slot-table source information and registers
the settings section.

### Packages (`io.thernal.console.inspector.ui`)

| Package | Pieces |
|---------|--------|
| `host` | `InspectorHost` wraps the app content. Provides `LocalInspectionTables` so Compose hands every sub-composition's slot table to the engine, hands the root composition data over, starts `InspectorEngine.run()` and applies the font scale and layout direction overrides. Its structure never changes with the config, so toggling settings never resets app state. |
| `engine` | `InspectorEngine`, the single owner of runtime state (snapshot, selection, inspect mode, freeze, counts, flashes, timing) as `StateFlow`s. `run()` launches five loops: observer tracking, event consumption, command consumption, auto-refresh and a 250 ms stats loop that publishes counts and rebuilds the tree when new structure appeared. `InspectorPicker` turns touches into a selection, `PickChain` implements the step-in picking, `InspectorMeasure` holds the ruler state, `InspectorQueries` and `NodeDetailsReader` build what the tab shows. |
| `engine/tree` | `TreeBuilder` walks the slot tables: every group whose source information names a call becomes an `InspectorNode`, layout nodes attach to their nearest named owner, and recompose scopes are indexed to nodes. `InspectorSnapshot` is the immutable result; `liveBounds()` reads every node's frame from live coordinates, so the overlay follows scrolling and animation without a rebuild. `SourceNaming` decides what is framework, generic or the inspector's own UI. |
| `engine/geometry` | `ModifierKind` classifies modifiers (padding, visual, other); `NodeBox` splits a node into frame, margin and padding; `PickRect` lets a touch on a margin pick that node; `NodeDistance` measures gaps and insets. |
| `engine/recomposition` | `RecompositionObserver` observes every running `Recomposer` and only enqueues events, since callbacks arrive on composition threads. `ScopeSkip` reads the runtime's internal skip flag; `RecompositionCounts` keeps per-scope counts, moves the counts of scopes that left the tree onto their node, and credits hidden framework nodes to the app composable above them. |
| `overlay` | `InspectorBackdrop` / `InspectorDrawLayer` / `DrawInspectorScene` draw on one `Canvas`, redrawn every frame (`FrameTick`) only while there is something live to draw, capped at 600 nodes and 150 labels. `InspectorCapture` / `InspectorPickLayer` swallow pointer input while inspecting. |
| `dock` | The widget (status *Attention* while picking), its chip panel, the folded-dock quick action and the summary line. |
| `view/inspector`, `navigation` | The tab: `InspectorViewModel` combines the engine flows into tree rows, details and ranking; `TreeExporter` produces the copied text. |

### Keeping the numbers honest

- The engine ignores scopes that belong to the inspector's own overlay files, and does not count composition time for
  passes that ran no app scope, so its own redraws never feed back into the statistics.
- While the tab is open, unknown scopes do not mark the tree dirty: the tab's own lists would otherwise trigger
  rebuilds in a loop.
- Automatic rebuilds keep a gap of at least 500 ms, or eight times the last build's duration if that is longer.
  *Freeze* stops them; an explicit refresh or a pick still rebuilds.
