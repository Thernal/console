# console-inspector-core

The UI inspector's public API, without Compose:

- `ConsoleInspector`: `config: StateFlow<InspectorConfig>`, `updateConfig { … }` (values are clamped to their ranges)
  and `dispatch(InspectorCommand)` (`ResetStats`, `Refresh`, `ClearSelection`);
- `InspectorConfig` and the override enums `InspectorFontScale`, `InspectorLayoutDirection`.

```kotlin
implementation("io.github.thernal:console-inspector-core:<version>")
```

The engine in `console-inspector-ui` reads the config and consumes the commands. Features, settings and architecture
are described in [`inspector-ui`](../inspector-ui/README.md).
