# console-agent-ui

The agent bridge: carries what the addons registered in [`agent-api`](../agent-api/README.md) to the
[`console-mcp`](../../console-mcp/README.md) hub on the developer's machine, so an AI coding agent (Claude Code or any
other MCP client) can read the app's logs and state and, when allowed, act on it. The bridge captures nothing itself;
each addon decides what it shares.

It comes with `console-bundle-ui`. With hand-picked modules, add it to debug builds:

```kotlin
debugImplementation("io.github.thernal:console-agent-ui:<version>")
```

No code required: the addon installs itself on Android, iOS and JVM. To leave it out, exclude `console-agent-ui` from
the bundle, or turn off *Share with AI agents* in the Settings tab. Setting up the hub on your machine:
[`console-mcp`](../../console-mcp/README.md#set-up).

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
| [Details](../details-ui/README.md#agent) | `details` | `details`, `details_changes` feed |
| [Settings](../settings-ui/README.md#agent) | `settings` | `settings`, actions `settings_set`, `settings_reset`; the `agent` section is read-only |
| [UI inspector](../inspector-ui/README.md#agent) | `inspector` | `ui_tree`, `ui_node`, `ui_find`, `recompositions`, actions `ui_font_scale`, `ui_layout_direction`, `recompositions_reset` |
| [Stepper](../stepper-ui/README.md#agent) | `stepper` | `stepper_state`, `stepper_events` feed, actions `stepper_pause`, `stepper_next`, `stepper_resume` |
| Floating dock (this addon) | `dock` | `dock_state`, actions `dock_move`, `dock_minimize`, `dock_select` |

### Floating dock

Console's floating dock shares the screen with the app, so it can cover what a UI test taps. The dock belongs to
`console-ui`, not to an addon, so this addon registers its entries.

| Tool | What it does |
|------|--------------|
| `dock_state` | Whether the dock is on screen (and if not, why), its edge, height (`top`, `center`, `bottom` or a percentage), bounds in window pixels, minimized or open on which widget, and every widget with its hidden flag. |
| `dock_move(edge, vertical)` | Action. Puts the dock flush with `left` or `right` at `top`, `center` or `bottom`, clamped inside the window. |
| `dock_minimize(minimized)` | Action. Folds the dock into its button, or opens its card. |
| `dock_select(widget)` | Action. Opens the card on a widget's tab, by id or title. |

Each action answers with the state once the dock has stopped moving, so the bounds are where it ended. Nothing is
saved: the next launch puts the dock back where it starts, and a drag by hand moves it again. Showing and hiding stay
in Settings: `settings_set(section="dock", key="isVisible", value="false")`, or `hiddenWidgetIds` for one widget. While
the dock is hidden the actions answer with an error that names this setting.

A test that taps near the top right corner: `dock_state`, then `dock_move(edge="left", vertical="bottom")`, and
`dock_move(edge="right", vertical="top")` after it.

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
| `addon/AgentAddon` | `onInstall` registers the Settings section and the floating dock's entries, and starts the bridge. |
| `addon/AgentSettingsSection` | The **Agent** settings section. |
| `addon/DockAgentEntries`, `addon/DockAgentText` | The floating dock's entries and the `dock_state` text. |
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
