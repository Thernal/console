# console-network

Captures HTTP traffic and shows it in the regular log list: method, path, status, duration, headers and body, with a
detail screen for each request and response.

| Module | What it is |
|--------|------------|
| `console-network-core` | `NetworkLog` (`Request` / `Response`), `SensitiveHeaders`, the runtime `NetworkConfig`, body classification. No UI, no HTTP client. |
| `console-network-ktor` | `ConsoleNetworkKtorPlugin`, a Ktor client plugin (all platforms). |
| `console-network-okhttp` | `ConsoleNetworkOkHttpInterceptor`, an OkHttp interceptor (Android and JVM). |
| `console-network-ui` | The renderer for network logs and the **Network** settings section. Auto-installs. |

## Install

Pick the integration that matches your client; the bundles do not include either.

```kotlin
debugImplementation("io.github.thernal:console-network-ui:<version>")
debugImplementation("io.github.thernal:console-network-ktor:<version>")    // or
debugImplementation("io.github.thernal:console-network-okhttp:<version>")
```

```kotlin
val ktor = HttpClient { install(ConsoleNetworkKtorPlugin) }

val okHttp = OkHttpClient.Builder()
    .addInterceptor(ConsoleNetworkOkHttpInterceptor())
    .build()
```

There is no no-op artifact: install the plugin or interceptor only in debug code.

## Sensitive headers

`Authorization`, `Cookie`, `Set-Cookie`, `X-Api-Key` and `Proxy-Authorization` are masked with `***` before the log
is created, so the real value never enters the pipeline. Change the set when installing:

```kotlin
HttpClient {
    install(ConsoleNetworkKtorPlugin) {
        sensitiveHeaders = SensitiveHeaders(names = setOf("authorization", "x-session-token"))
    }
}

ConsoleNetworkOkHttpInterceptor(sensitiveHeaders = SensitiveHeaders.NONE)   // mask nothing
```

The value passed at install time only seeds `NetworkConfig`. Both integrations read `NetworkConfig.config` on every
request, so the *Sensitive headers* setting (or `NetworkConfig.updateConfig { … }`) applies to the next request
without rebuilding the client. Names are matched case-insensitively; write them in lower case when calling
`NetworkConfig` directly (the settings screen lower-cases them for you).

> Only the header **names** are carried into `NetworkConfig`. A custom `SensitiveHeaders.mask` is currently not
> applied: masked values always read `***`.

## Settings

Section **Network** (`id = "network"`): `sensitiveHeaders`, the masked header names.

## What it shows

- **List item**: chips for *Request*/*Response*, the method and the status, the time, the message (`GET path → 200
  (143ms)`) and a meta line (header count, body length, duration), tinted by level. A request is `Info`; a response's
  level comes from its status: `< 300` Success, `3xx` Info, `4xx` Warning, `5xx` and failures Error.
- **Detail**: the request and its response share a `groupId`, so the detail pager shows both as pages. Each page has a
  summary, collapsible headers and body, and copy actions for headers, body and, on a request, a `curl` command.
- **Bodies** go through `resolveNetworkBody`: JSON (by `Content-Type`, or by content when there is none) is
  pretty-printed, binary types (`image/*`, `application/pdf`, protobuf…) and undecodable text show their type and
  size instead of bytes.

## Architecture

```
HTTP client ─► Ktor plugin / OkHttp interceptor ─► Console (NetworkLog.Request, then NetworkLog.Response)
                         ▲ reads NetworkConfig per request          │
                         │                                          ▼
              Settings ─► NetworkConfig            logging-ui list ─► NetworkLogRenderer (by KClass)
```

| Piece | Role |
|-------|------|
| `network-core/NetworkLog` | Sealed `Log` subtype with tag `Network`. Overrides `toShareText` (the copy/share format) and `contains` (method, URL, headers, body, status). The Logs search matches the message only, which holds the method, path and status. |
| `network-core/NetworkConfig` | Runtime config (`sensitiveHeaders`) shared by both integrations. |
| `network-core/NetworkBodyExtensions` | `resolveNetworkBody`, `toCopyText`, `toHumanReadableSize`; JSON formatting is a small dependency-free reformatter. |
| `network-ktor/ConsoleNetworkKtorPlugin` | Hooks `Send`: logs the request, proceeds, saves the call so the body can be read without consuming it, logs the response. Errors with a response (`ResponseException`) and transport failures are logged and rethrown. Uses `asyncNotify`. |
| `network-okhttp/ConsoleNetworkOkHttpInterceptor` | Same flow with `chain.proceed`; reads the response with `peekBody(maxBodyBytes)` (64 KB by default, a constructor parameter). Uses `blockingNotify`. |
| `network-ui/NetworkAddon` | Registers `NetworkLogRenderer` for both `NetworkLog.Request` and `NetworkLog.Response`, and the settings section. No tab of its own: entries appear in the Logs tab. |

`console-crash-report-ui` ships codecs for both network log types, so network logs restored from a crash session keep
their full detail.
