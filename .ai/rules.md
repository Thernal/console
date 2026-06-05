# Console — AI Coding Rules

Kotlin Multiplatform debug console library (Android + iOS + JVM).

## Codebase exploration

Before answering architectural or cross-module questions, check in order:

1. **`/graphify` skill not available** — inform the user and offer a choice:
   - A) Install graphify, then continue
   - B) Continue without it (slower, file-by-file exploration)

2. **Skill available but `graphify-out/` missing** — ask the user:
   - A) Run `/graphify` now to generate the knowledge graph
   - B) Continue without it

3. **`graphify-out/` exists** — use it:
   - Read `graphify-out/GRAPH_REPORT.md` for the navigation index and community hubs.
   - Do not explore the source tree file-by-file when the graph already maps the relationships.

## Convention plugins

From `build-logic` — use these in every module's `build.gradle.kts`:
- `convention.lib.core` — KMP multiplatform (Android + JVM + iOS), no Compose
- `convention.lib.ui` — extends core, adds Compose + navigation3
- `convention.publish` — Maven publishing

Current modules and addons are auto-discovered; read `settings.gradle.kts` for the authoritative list.

## Addon pattern

Every addon follows this structure:
- **`-core` / `-api`** — `Log.Custom` subtype + state (lib.core, no UI)
- **`-ui`** — `LogRenderer` implementation registered in `LogRendererRegistry`, auto-init (lib.ui)
- **`-noop`** — production stub with same API, empty bodies (lib.core)

```kotlin
// In addon -ui's onInstall:
LogRendererRegistry.register<MyLog>(MyLogRenderer)

// LogRendererRegistry dispatches by exact KClass — O(1), no canRender() needed
// DispatchLogRenderer in ConsoleProvider wraps the user-provided renderer and
// checks the registry first, falls back to the provided renderer for Log.Basic
```

Auto-init wiring:
- Android: `ConsoleAutoInitProvider` subclass + `AndroidManifest.xml` `<provider>` entry
- iOS/native: top-level `@EagerInitialization` property using `consoleAddonInit { }`

## After every implementation

After completing any code change, run the compile task for the affected modules and fix all errors before reporting the task as done:

```bash
./gradlew :module-name:compileDebugKotlin        # Android
./gradlew :module-name:compileKotlinJvm          # JVM
./gradlew :module-name:compileKotlinIosArm64     # iOS
```

If unsure which targets are relevant, run all:

```bash
./gradlew :module-name:assemble
```

Do not report the task as done while there are unresolved compilation errors.

## Detekt

Read `.ai/detekt-rules.md` for the active rule summary before writing any code.

## UI conventions

- Use `DsScaffold`, `DsAppBar`, `DsCard`, `DsChip`, `DsText`, `DsIcon`, `DsIconButton`, `DsDivider` from `designsystem.components`.
- Access theme via `Theme.colors`, `Theme.typography`, `Theme.dimens`, `Theme.metrics`, `Theme.rounding`.
- State: extend `ViewState`, expose `StateField<T>` via `field(initial)`. Mutate only through `StateHolder.set` / `StateHolder.update`.
- ViewModels: extend `ViewModel()`, implement `StateHolder`, collect flows in `viewModelScope.launch`.
- `@Composable` functions are exempt from `LongMethod`, `CyclomaticComplexMethod`, `CognitiveComplexMethod`, `MagicNumber`, `TooManyFunctions`, and `FunctionNaming` rules.
