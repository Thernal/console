# console-network-core

Shared network types, without UI or an HTTP client:

- `NetworkLog.Request` / `NetworkLog.Response`, the `Log` subtypes the integrations emit;
- `SensitiveHeaders` and the runtime `NetworkConfig`;
- `resolveNetworkBody` / `NetworkBody` (JSON, text or binary), `toCopyText`, `toHumanReadableSize`, `toNetworkLevel`.

```kotlin
implementation("io.github.thernal:console-network-core:<version>")
```

The integrations depend on it, so you rarely add it yourself. See [`network-ui`](../network-ui/README.md) for setup
and architecture.
