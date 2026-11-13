# Changelog

What is new in each release of Console. A release's notes on GitHub are its section here, then its commits
(`.github/release-notes.sh`); a release tag whose version has no section here publishes nothing. Write what changed
for someone using Console, not for someone reading the code; the commits follow anyway. Releases before 1.0.0-rc.1
have their notes on their GitHub releases only.

## 1.0.0-rc.1 — 2026-11-13

- **AI coding agents read the running app.** The agent addon (`console-agent-api`, `console-agent-ui`, both in
  `console-bundle-ui`) shares what each addon sees with Claude Code or any MCP client, through `console-mcp`, a hub on
  your machine: logs, HTTP calls, crash sessions, details, settings, the floating dock, the stepper and the composable
  tree. An agent waits for a log after a code change and reads what the app wrote before it crashed; it changes the
  app (settings, the stepper, the dock, a log) only while *Allow actions* is on. Your own code shares more with
  `agentEntries`. README → AI agents (MCP).
- **console-mcp comes with every release.** Download `console-mcp.zip` from the release, unzip it and register
  `console-mcp/bin/console-mcp` with your agent; it needs Java 17 or newer, and `console-mcp --version` names its
  release. An app whose Console speaks another version of the hub's messages is listed in `devices` with what to
  update, instead of being misread.
- **`blockingNotify` no longer freezes the app while the stepper is paused.** On the main thread it queues the log as
  `notify` does and returns at once; on other threads it still waits for the observers.
- **Navigation3 is stable: 1.1.1.** Console's navigation API (`ConsoleNavigator`, `ConsoleNavGraph`, `ConsoleRoute`)
  is built on it, so your app resolves Navigation3 1.1.1 or newer; Compose Multiplatform 1.10 is still enough. iOS
  simulators on Intel Macs (`iosX64`) are no longer a target: stable Navigation3 is not published for them.
- **The public API is checked.** Every published module keeps the API it publishes in `api/`; a change to it shows in
  the diff and fails the build until it is written there on purpose.
