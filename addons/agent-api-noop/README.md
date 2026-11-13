# console-agent-api-noop

Release stand-in for [`console-agent-api`](../agent-api/README.md): `agentEntries`, `AgentRegistry` and `ConsoleAgent`
keep their signatures, but nothing is registered, `AgentRegistry.entries` stays empty and no connection is made. Code
that shares state with an agent compiles unchanged in release builds.

```kotlin
releaseImplementation("io.github.thernal:console-agent-api-noop:<version>")
```
