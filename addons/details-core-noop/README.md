# console-details-core-noop

Release stand-in for [`console-details-core`](../details-core/README.md): the same `ConsoleDetails.put` / `remove` /
`clear` and `ConsoleKey` API with empty bodies, so call sites compile unchanged and nothing is stored.

```kotlin
releaseImplementation("io.github.thernal:console-details-core-noop:<version>")
```

It has no `ConsoleDetails.flow`. See [`details-ui`](../details-ui/README.md) for the full addon.
