# console-agent-api

Contract module for the agent bridge: what an addon, or the host app, shares with an AI coding agent over MCP. Addons
register tools and feeds here the same way they register settings in [`settings-api`](../settings-api/README.md). No
socket and no UI: [`agent-ui`](../agent-ui/README.md) carries the registry to the [`console-mcp`](../../console-mcp/README.md)
hub on the developer's machine. Without it, registrations sit inert.

```kotlin
implementation("io.github.thernal:console-agent-api:<version>")
// release: same API, registrations are dropped
releaseImplementation("io.github.thernal:console-agent-api-noop:<version>")
```

---

## Registering entries

Register from `ConsoleAddon.onInstall()`, or from the host app for its own state:

```kotlin
@file:OptIn(ConsoleInternalApi::class)

AgentRegistry.register(
    agentEntries(
        id = "cart",                                   // unique per app
        title = "Cart",
        about = "The shopping cart and its checkout",  // one line; joins the MCP server's instructions
    ) {
        feed(name = "cart_events", description = "Cart changes, newest last", records = cartEvents)

        tool(name = "cart_state", description = "Items in the cart with prices") {
            val limit = int(key = "limit", description = "How many items", default = 20, min = 1, max = 100)
            run { call -> cart.items.take(call[limit]).joinToString("\n") { "${it.sku} ×${it.count} ${it.price}" } }
        }

        action(name = "cart_add", description = "Adds an item to the cart") {
            val sku = text(key = "sku", description = "Product SKU")
            val count = int(key = "count", description = "How many", default = 1, min = 1, max = 99)
            run { call -> cart.add(sku = call[sku], count = call[count]).let { "Cart has ${it.size} items" } }
        }

        redact("voucher")
    },
)
```

| Entry | What the agent gets |
|-------|---------------------|
| `tool(name, description) { … }` | A read-only MCP tool. Its `run` body answers from the current state, as text. |
| `action(name, description) { … }` | An MCP tool that changes the app. Offered only while *Allow actions* is on, which it is not by default. |
| `feed(name, description, records)` | Three tools derived by the hub: `<name>` (filtered list), `<name>_get` (one record in full) and `<name>_wait` (waits for a matching record). Records reach the hub as they happen, so they stay readable after the app crashed. |
| `logFeed(name, description, logs)` | A `feed` of Console logs; each log becomes a record through the format `describe` registered for its type. |
| `describe<T>(summary, detail, fields)` | How logs of exactly `T` look in every log feed. Without one: the first message line, `toShareText()`, no fields. |
| `redact(vararg keys)` | Keys whose values never leave the app, in every feed and tool result. |

### Tool arguments

Each argument function returns a typed handle; read it in `run` with `call[handle]`.

| Function | Value | Notes |
|----------|-------|-------|
| `text(key, description)` | `String` | Required. |
| `text(key, description, default)` | `String` | |
| `optionalText(key, description)` | `String?` | Null when the agent leaves it out. |
| `int(key, description, default, min, max)` | `Int` | A value out of `[min, max]` is refused with a message, not clamped. |
| `optionalInt(key, description, min, max)` | `Int?` | |
| `toggle(key, description, default)` | `Boolean` | |
| `choice(key, description, options, default)` | `String` | Matched ignoring case. |
| `optionalChoice(key, description, options)` | `String?` | |
| `enum<E>(key, description, default)` | `E` | A `choice` over the constants of `E`. |

### Rules

- Tool and feed names are `lower_snake_case` (up to 48 characters) and unique across the whole app, so prefix them with
  the addon: `network_hosts`, not `hosts`. A feed also takes its `_get` and `_wait` names. `devices` is reserved.
- Argument keys start with a lower-case letter; write them in `snake_case` like the hub's own (`limit`,
  `timeout_seconds`). `device` is reserved, because the hub adds it to every tool to pick the app.
- Tool bodies run on `Dispatchers.Default`, one coroutine per call, and time out after 15 seconds; switch to
  `Dispatchers.Main` inside the body when the state lives there. An exception becomes an error result for the agent.
- Answer compactly, one line per item: the agent pays for every token. Put the full text behind a `_get` or a separate
  tool instead of in a list.
- A feed's `records` should be a hot flow whose producer never suspends (a `MutableSharedFlow` that drops the oldest
  entries), so a slow or missing hub cannot slow the app down. A replay buffer lets the bridge pick up records emitted
  before it started.

### Records

`AgentRecord` is what a feed carries: `id`, `timeMillis`, `summary` (one line, shown in lists), `detail` (full text, shown
by `_get`), `level`, `tag`, `kind` and string `fields`. The hub filters on `level` (Console's `LogLevel` names), `tag`,
`kind` and text in `summary` and `detail`.

### Redaction

Redaction runs inside the app, before anything leaves it. A key masks:

- JSON members: `"refreshToken": "…"` and non-string values,
- query strings and forms: `token=…`,
- header lines: `Authorization: …`,
- record `fields` whose name contains it.

A key matches every name that contains it, ignoring case. The defaults are `authorization`, `cookie`, `password`,
`secret`, `token`, `apikey`, `api_key` and `api-key`; every `redact(...)` adds to them for the whole app.

## Configuration

`ConsoleAgent.config` is a `StateFlow<AgentConfig>`; change it with `ConsoleAgent.updateConfig { copy(...) }`.
`agent-ui` binds the first two fields to its Settings section.

| Field | Default | What it does |
|-------|---------|--------------|
| `enabled` | `true` | Connect to the hub and share. |
| `enableActions` | `false` | Offer actions to the agent and run them. |
| `hubHost` / `hubPort` | `127.0.0.1` / `47474` | Where the hub listens. |
| `deviceName` | platform name | Name in the hub's device list. |
| `historySize` | `2000` | Feed records kept for a hub that connects later (100–20 000). |

`ConsoleAgent.connection` reports `Disabled`, `Searching` or `Connected(hubAddress)`.

## Architecture

| Type | Role |
|------|------|
| `agentEntries` → `AgentEntries` | One section's share: tools, feeds, log formats and redacted keys. Names, keys and duplicates are checked when it is built. |
| `AgentRegistry` | `@ConsoleInternalApi`; `entries: StateFlow`. `register` fails fast on a duplicate section `id`, a name another section already uses, or a reserved name. |
| `AgentTool`, `AgentParam`, `AgentArg`, `AgentCall` | A tool, its typed parameters, the handles `run` reads them with, and one call's values. |
| `AgentFeed`, `AgentRecord`, `AgentLogFormat` | Feeds, their records, and how a log type becomes a record. |
| `ConsoleAgent`, `AgentConfig`, `AgentConnection` | The bridge's configuration and connection state. |

Sections can be registered at any time: the bridge re-sends what the app shares, and the hub tells the agent that its
tool list changed.

Addons register against `AgentRegistry` directly instead of through a `ConsoleAddon` hook, which keeps `console-api`
free of an `agent-api` dependency, as with `settings-api`.
