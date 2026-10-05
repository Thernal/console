# console-inspector-core-noop

Release stand-in for [`console-inspector-core`](../inspector-core/README.md): `ConsoleInspector` keeps its API, but
`config` always holds the defaults, `updateConfig` and `dispatch` do nothing, and no UI is pulled in.

```kotlin
releaseImplementation("io.github.thernal:console-inspector-core-noop:<version>")
```

See [`inspector-ui`](../inspector-ui/README.md) for the full addon.
