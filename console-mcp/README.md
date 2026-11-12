# console-mcp

Desktop side of the Console agent bridge: an MCP server (stdio) for AI coding agents, and a hub (TCP on
`127.0.0.1:47474`) that apps with [`console-agent-ui`](../addons/agent-ui/README.md) connect to. The agent sees the tools
and feeds the app's addons registered in [`console-agent-api`](../addons/agent-api/README.md); the hub keeps feed
records on the Mac, so they stay readable after the app crashed or was killed.

## Build

```bash
./gradlew -p console-mcp installDist
# → console-mcp/build/install/console-mcp/bin/console-mcp [--port 47474]
```

## Use with Claude Code

```bash
claude mcp add console -- "$PWD/console-mcp/build/install/console-mcp/bin/console-mcp"
```

Any MCP client that starts a stdio server works the same way.

## Tools

The tool list follows the connected apps:

| Tool | Comes from | Arguments |
|------|------------|-----------|
| `devices` | Built in | — |
| `<tool>` | A `tool` or `action` an app registered | Its own arguments, plus `device` |
| `<feed>` | A `feed` or `logFeed` | `device`, `level` (minimum), `tag`, `kind`, `query` (text in summary or detail), `after` (seq cursor), `limit` (default 50, max 500) |
| `<feed>_get` | Same feed | `device`, `id` (a record id or its `#seq`) |
| `<feed>_wait` | Same feed | The filters of `<feed>`, plus `timeout_seconds` (default 10, max 60); `after` defaults to the newest seq |

`device` is an id or part of a device name, and may be left out when only one connected app offers the tool. Feed
tools stay listed after their app disconnects; its tools and actions do not. Actions appear only while the app's
*Allow actions* setting is on, and their description starts with `[Action: changes the app]`.

When an app connects, registers more, or turns actions on or off, the server sends `notifications/tools/list_changed`,
and the agent reads the new list. The server's instructions also carry each section's `about` line, so the agent knows
what the apps share before it calls anything.

## Reaching the hub

| App runs on | How it reaches the hub |
|---|---|
| JVM desktop, iOS simulator | `127.0.0.1` directly |
| Android emulator or USB device | `adb reverse tcp:47474 tcp:47474` |

## Try it without a phone

```bash
./gradlew :sample:agent-jvm:installDist
sample/agent-jvm/build/install/agent-jvm/bin/agent-jvm 120 --actions-after 30
```

The sample counts up once a second for 120 seconds. It shares an `events` feed, a `sample_state` tool and a
`sample_reset` action, and turns actions on after 30 seconds (`--actions` turns them on from the start).

## Limits

- One hub per `console-mcp` process: a second agent session on the same port cannot bind it and says so.
- The hub keeps the newest 20 000 records per device and feed, in memory, for as long as the process runs.
- A call to an app times out after 20 seconds; the app gives its tool 15 of them.
