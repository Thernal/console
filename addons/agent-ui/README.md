# console-agent-ui

The agent bridge: carries what the addons registered in [`agent-api`](../agent-api/README.md) to the
[`console-mcp`](../../console-mcp/README.md) hub on the developer's machine, so an AI coding agent (Claude Code or any
other MCP client) can read the app's logs and state and, when allowed, act on it. The bridge captures nothing itself;
each addon decides what it shares.

```kotlin
debugImplementation("io.github.thernal:console-agent-ui:<version>")
```

No code required: the addon installs itself on Android, iOS and JVM. It is not part of `console-bundle-ui`; add it on
purpose, to debug builds only.

---

## Connecting

The app keeps trying to reach the hub at `127.0.0.1:47474` (at most every 2 seconds) while sharing is on, so it does not
matter whether the app or the agent session starts first.

| App runs on | How it reaches the hub |
|-------------|------------------------|
| JVM desktop, iOS simulator | `127.0.0.1` directly |
| Android emulator or USB device | `adb reverse tcp:47474 tcp:47474` |
| Real iPhone | Not supported yet |

Several apps can connect to one hub at once; the agent picks one with the `device` argument.

## What the addons share

Each addon registers its own entries; installing it is enough.

| Addon | Section | Tools |
|-------|---------|-------|
| [Logs](../logging-ui/README.md#agent) | `logging` | `logs` feed, `logs_summary`, action `logs_write` |
| [Network](../network-ui/README.md#agent) | `network` | `network` feed, `network_hosts`; network logs in every log feed |
| [Crash reports](../crash-report-ui/README.md#agent) | `crash-report` | `crashes` feed, `crash_sessions`, `crash_session`, actions `crash_delete`, `crash_clear` |

## Settings

Section **Agent** in the Settings tab (order 60), bound to `ConsoleAgent.config`:

- *Share with AI agents* (`enabled`, on): connect to the hub.
- *Allow actions* (`enableActions`, off): offer the tools that change the app.

The hub address, device name and history size are set in code with `ConsoleAgent.updateConfig`.

## Security

- Debug builds only. Release builds use `agent-api-noop`, so nothing connects.
- The hub listens on the loopback interface; Android reaches it through `adb reverse`. Nothing listens on the device.
- Actions are left out of what the app shares while *Allow actions* is off, and a call to one is refused.
- Redaction runs in the app before any feed record or tool result leaves it: the default keys plus every
  `redact(...)` (see [`agent-api`](../agent-api/README.md#redaction)).

## How it works

```
addon.onInstall ─► AgentRegistry.register(entries)
                              │ entries (StateFlow)
                              ▼
AgentBridge ──┬─ feed records ─► redact ─► seq ─► history ring + live stream ─┐
              ├─ manifest (tools, feeds, actions on/off) ─────────────────────┼─► TCP frames ─► console-mcp hub
              └─ call ◄── hub ─► AgentCallHandler ─► result ──────────────────┘
```

- **Feeds.** One collector per feed. Each record is redacted, numbered and kept in a ring of the last
  `historySize` records. A hub that connects gets the ring first, then the live stream, and drops duplicates by number,
  so records from before the hub started still arrive. A slow or missing hub costs dropped records, never a slow app.
- **Manifest.** Sent on connect and again whenever the registry or the actions switch changes; the hub turns it into
  MCP tools and tells the agent that the list changed.
- **Calls.** Each call runs in its own coroutine: the actions switch is checked, the arguments are validated against
  the tool's parameters, the body runs with a 15-second timeout and the result is redacted.

Wire format: a 4-byte big-endian length, then UTF-8 JSON. App to hub: `hello` (device), `manifest`, `record`, `result`.
Hub to app: `call`.

| Piece | Role |
|-------|------|
| `addon/AgentAddon` | `onInstall` registers the Settings section and starts the bridge. |
| `addon/AgentSettingsSection` | The **Agent** settings section. |
| `bridge/AgentBridge` | Collects feeds, keeps the connection, sends manifests and records, serves calls. |
| `bridge/AgentMessages` | Encodes the JSON messages. |
| `bridge/AgentFrames`, `bridge/AgentFrameWriter` | Length-prefixed frames; one writer at a time. |
| `bridge/AgentHistory` | Ring of recent records for a hub that connects later. |
| `bridge/AgentRedactor` | Masks sensitive values in text and fields. |
| `bridge/AgentArguments` | Validates a call's arguments: required values, ranges, options. |
| `bridge/AgentCallHandler` | Runs one call and builds its result. |
| `device/AgentDevice` | Device id, run id, name, platform, OS version and app id per platform. The device id is kept in the app's storage on Android and iOS (`device/StoredDeviceId`), so a restarted app is the same device to the hub; on JVM it is new per process, since two copies of a desktop app can run side by side. The run id is new per process. |

Dependencies, in this module only: `ktor-network` for the raw TCP socket (the catalog's ktor) and
`kotlinx-serialization-json` for the JSON element API (no compiler plugin).
