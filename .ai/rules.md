# Console — AI Coding Rules

Kotlin Multiplatform debug console library (Android + iOS + JVM). Compose Multiplatform UI,
addon-based architecture, no DI framework, no flavors/localization/network/Room.

## Working agreement (session & delivery)

- Reply in the user's language; keep all in-repo content — code, identifiers, comments, docs,
  commit messages — in English unless a file already uses another language.
- Do not commit by default; leave the change as a patch in the working tree. Commit only when
  the user explicitly asks (see `## Commits`).
- Never modify, revert, reformat, stage, or discard unrelated user changes unless the user asks
  for that exact action.
- If a project pattern is unknown, do not invent one silently: search for an existing example
  first, and if none exists, present a short solution list with tradeoffs instead of guessing.
- For unclear context, state your assumptions before implementing.
- End substantial responses with: **Summary · Changed files · Verification · Risks / skipped checks.**
- For code changes, include a mini review diff: **Description · Rationale · Alternatives considered.**

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
   - Treat the graph as a snapshot: for any area changed after it was built (compare
     `graphify-out/manifest.json` against `git log`), trust the working tree. Refresh a stale
     area with `/graphify . --update`, or note the staleness.
   - Do not bulk-read the source tree file-by-file when the graph already maps the relationships.

Before editing, also run `git status --short --branch` and preserve unrelated user changes.

## Change strategy

- Do not add a new abstraction by default; follow existing project patterns. If a new abstraction
  looks useful, present a short option list with rationale first.
- Before adding a wrapper, helper, or abstraction, check whether an existing component/API can do
  the same with a small parameter or call-site change. Avoid wrappers that only pass through
  existing parameters.
- Prefer the smallest layer-correct change over a broad refactor; avoid unrelated cleanup.
- One class — or one non-preview `@Composable` — per Kotlin file. Preview composables are exempt
  and may live next to the component they preview.
- Public functions use explicit, descriptive names. Prefer concise names when the type/parameter
  context already conveys detail; avoid redundant suffixes (e.g. `...AsStringMap` when the return
  type is already `Map<…, String>`).
- Callback names describe the local component event, not the parent's use case or resulting
  action: prefer `onLogClick(log)` over `onNavigateToDetail(log)` unless the component itself owns
  navigation.
- No magic numbers/strings; use named constants, existing constant holders, or typed values.
  Search for an existing shared holder before adding a new constant.
- Before adding a dependency, try to solve it with existing ones. If still needed, document
  rationale, alternatives considered, risk, and build/binary impact. Do not edit the version
  catalog for a one-off dependency.

## Convention plugins

Discover which `build-logic` convention plugins exist and how a module applies them via graphify
(`/graphify query "build-logic convention plugins and module build setup"`) or by reading
`build-logic` and `settings.gradle.kts` (the authoritative module list — addons are
auto-discovered). Match a neighboring module of the same kind.

- Use type-safe project accessors (`projects.xxx`), never `project(":xxx")`.

## Architecture & module placement

Discover the current module topology and dependency edges via graphify
(`/graphify query "module topology and dependency direction"`) before cross-module work. Then:

- Preserve dependency direction. `console-core` holds the cross-cutting primitives (log model,
  `ConsoleScope` / `LogObserver` / `LogProcessor`, `ConsoleInternalApi`). `console-runtime` (the
  `Console` pipeline) and `console-api` / `console-ui` (view layer) both depend on `console-core`,
  NOT on each other — the view layer must stay independent of the observer pipeline. Do not push
  business-specific logic into these modules for convenience.
- Addon UI stays inside its own `-ui` module; addons do not depend on each other's internals.
- Cross-addon communication goes through `console-api` contracts and the registries, not direct
  module-to-module coupling.
- If a new module is needed, explain the reason before creating it.

## Addon pattern

Before adding or changing an addon, study an existing one via graphify
(`/graphify query "addon module structure, LogRendererRegistry registration, and auto-init wiring"`)
or by reading an existing `addons/*-ui` module and mirroring it. Hold these invariants:

- Split into `-core` (custom `Log` subtype + state, no UI), `-ui` (a `LogRenderer` registered in
  `LogRendererRegistry`, auto-initialized), and `-noop` (same API, empty bodies).
- `ConsoleAddon.onInstall()` takes no parameters and must stay Console-free. Addons that capture
  logs reference the `Console` singleton (`console-runtime`) DIRECTLY in `onInstall` and declare a
  `console-runtime` dependency; view-only addons leave `onInstall` empty and depend only on
  `console-api` / `console-core`.
- Registration APIs (`Console.addObserver`, `LogRendererRegistry.register`) are `@ConsoleInternalApi`
  (opt-in, error level) — opt in with `@file:OptIn(ConsoleInternalApi::class)`. `Console.seal()`
  locks the observer pipeline against later injection (use after first-party setup in release).
- `LogRendererRegistry` dispatches by exact `KClass` (O(1), no `canRender()`); `DispatchLogRenderer`
  checks the registry first and falls back to the provided renderer for `BasicLog`.
- Auto-init: Android via a `ConsoleAutoInitProvider` subclass + manifest `<provider>`; iOS/native
  via a top-level `@EagerInitialization` property using `consoleAddonInit { }`.

## UI conventions

Discover the available design-system components and theme tokens via graphify
(`/graphify query "design system Ds components and Theme tokens"`) or by reading
`designsystem/components`; reuse them before adding a new visual primitive. Then follow:

- Access theme only via `Theme.*` (colors, typography, dimens, metrics, rounding) — no hardcoded
  spacing, colors, or typography.
- The screen-top composable (`DsScaffold`) owns full-screen concerns such as blocking pointer
  input from leaking to content behind it; do not re-implement that blocking in components
  beneath it.
- State: extend `ViewState`, expose `StateField<T>` via `field(initial)`, and mutate only through
  `StateHolder.set` / `StateHolder.update` in response to intents — never from a composable.
- UI state must not hold mutable collections; expose immutable collections to Compose.
- ViewModels extend `ViewModel()`, implement `StateHolder`, and collect flows in
  `viewModelScope.launch`. Do not do heavy work, IO, or flow collection during recomposition.
- `@Composable` functions are exempt from `LongMethod`, `CyclomaticComplexMethod`,
  `CognitiveComplexMethod`, `MagicNumber`, `TooManyFunctions`, and `FunctionNaming`.

## Performance

- Keep UI state stable; prefer immutable collections for anything exposed to Compose state.
- Avoid O(n²) work over lists that can grow (log streams in particular).
- Flow collection must be lifecycle-aware and must not create duplicate subscriptions. Choose
  `collectLatest`, `stateIn`, `shareIn`, or debounce based on the pattern used by nearby code.
- Reuse existing debounce/cache/state-flow primitives instead of recreating them.

## Verification

After completing any code change, compile the affected modules and fix all errors before
reporting the task done:

```bash
./gradlew :module-name:compileDebugKotlin        # Android
./gradlew :module-name:compileKotlinJvm          # JVM
./gradlew :module-name:compileKotlinIosArm64     # iOS
```

If unsure which targets are relevant, run all:

```bash
./gradlew :module-name:assemble
```

- Run the smallest relevant module task first, then broaden if the blast radius is larger.
- Do not treat `compileKotlinMetadata` as sufficient for a KMP module; it can miss platform issues
  (e.g. unresolved imports in a platform source set). Always compile the real targets above.
- If a build cannot be run, state the reason in the final response.
- Do not report the task as done while there are unresolved compilation errors.

## Risk levels

- **Low** — docs, comments, isolated UI tweak.
- **Medium** — `ViewState`/`StateHolder` changes, addon UI, navigation routes.
- **High** — public API surface, addon registration / auto-init wiring (`LogRendererRegistry`,
  `ConsoleAutoInitProvider`, `@EagerInitialization`), pointer/gesture & nested-scroll behavior,
  and `convention.publish` / publishing config.
- For high-risk changes, write a short plan first and include a manual verification or rollback
  step.

## Commits

Apply only when the user explicitly asks to commit; otherwise leave the patch in the working tree.
If the user will commit themselves, present the recommended split and exact messages as a
suggestion.

- Split work into the smallest coherent changelists — one concern per commit. Do not mix
  refactor, feature, and fix; stage by hunk when one file spans concerns.
- Single-line messages, imperative mood, lower-case, English, no trailing period, ≤ 72 chars.
- Type prefix: `feat:`, `fix:`, `refactor:`, `redesign:`, `enhance:`, `chore:` (extended when they
  fit better: `docs:`, `test:`, `perf:`, `build:`, `ci:`, `style:`).
- Optional single scope when it adds clarity: `fix(console): ...`, `enhance(logging-ui): ...`.
- Mark breaking changes with `!` before the colon (`feat!:`, `refactor!:`) — public API,
  navigation route, or persisted-schema breaks.
- Keep generated output and formatting-only churn out of logic commits.
- **Commits are attributed to the user only. Do not add `Co-Authored-By` for the assistant and do
  not add "generated with" trailers.**
- Present the planned split (`<prefix>: <message>` + files per commit) before committing.

## Detekt

Read `.ai/detekt-rules.md` for the active rule summary before writing any code. Do not disable a
Detekt rule unless the reason is local, explicit, and defensible.

## Do not

- Revert or reformat unrelated user changes.
- Introduce a new pattern/abstraction when an existing component, registry, or state mechanism
  already solves the problem.
- Add cross-addon shortcuts that bypass `console-api` contracts or the registries.
- Add generated build output, IDE caches, or local machine paths.
- Re-implement pointer/scroll blocking inside components that sit beneath a `DsScaffold`.
