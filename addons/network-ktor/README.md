# console-network-ktor

`ConsoleNetworkKtorPlugin`, a Ktor client plugin that logs every request and its response to Console. Works on every
Ktor target.

```kotlin
debugImplementation("io.github.thernal:console-network-ktor:<version>")
```

```kotlin
val client = HttpClient {
    install(ConsoleNetworkKtorPlugin) {
        sensitiveHeaders = SensitiveHeaders(names = setOf("authorization", "x-session-token"))  // optional
    }
}
```

- Request bodies: `TextContent` is logged as text, byte arrays and streams as a placeholder (`<binary: N bytes>`,
  `<stream: …>`).
- Response bodies are read from a saved copy of the call, so your code can still read the body.
- A `ResponseException` is logged with its status and body, a transport failure as an `Error` response with the
  exception text; both are rethrown.
- Logs are sent with `Console.asyncNotify`, so the call waits for the pipeline (and for a paused stepper).

Masking, settings and rendering are described in [`network-ui`](../network-ui/README.md).
