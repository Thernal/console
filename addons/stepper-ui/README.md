# console-stepper-ui

Pauses the log pipeline and lets you release events one at a time, so an async flow that normally scrolls past in
milliseconds can be followed step by step. Holding a log also holds the code that emitted it when that code waits for
the pipeline (see [What pausing holds](#what-pausing-holds)).

```kotlin
debugImplementation("io.github.thernal:console-stepper-ui:<version>")
```

No code required: the addon installs itself on Android, iOS and JVM. There is no `-core` or `-noop` artifact, so keep
it out of release builds and do not reference `Stepper` from release code.

---

## Using it

- **Dock widget** (`id = "stepper"`): chips for *On/Off*, *Pause/Resume*, *Step* and *Tab*, plus the log currently
  held. The summary line reads `Off`, `Idle`, `Paused` or the status with `· N held`.
- While the pipeline is paused the widget's status is *Attention*: the folded dock shows a pill with **Step** and
  **Resume**, and the widget stays on screen even when floating widgets are hidden in Settings.
- **Stepper tab** (order 10): the events that were held, oldest first, each opening its detail screen
  (`SteppedLogRoute`). The app bar clears the list (tap twice to confirm).

```kotlin
// Programmatic control
Stepper.updateConfig { copy(enabled = true, paused = true, pauseOnMatch = true, pauseOnTags = setOf("Auth")) }
Stepper.dispatch(StepperIntent.Next)          // release the held event
Stepper.dispatch(StepperIntent.TogglePaused)
```

## Settings

Section **Stepper** (`id = "stepper"`), backed by `Stepper.config`:

| Key | Meaning | Default |
|-----|---------|---------|
| `enabled` | Stepper on or off | `false` |
| `paused` | Hold events until stepped (needs `enabled`) | `false` |
| `autoResumeSeconds` | Release a held event by itself after 3, 5, 10 or 30 s; *Off* waits forever | off |
| `pauseOnMatch` | Hold only events that match the filter below; otherwise every event is held | `false` |
| `pauseOnTags` | With a match filter, only these tags (empty = any tag) | empty |
| `pauseOnLevelAtLeast` | With a match filter, minimum level (`None`-level logs never match) | any |
| `maxSteppedEventCount` | How many held events the tab keeps (1–500) | `50` |

`paused` is forced off while `enabled` is off, so a paused pipeline always has a visible resume control.

## Agent

With [`console-agent-ui`](../agent-ui/README.md) installed, the addon lets an AI coding agent run an async flow step by
step (section `stepper`): pause on a tag, read the UI and the app's state while the flow is held, let it go, repeat.

| Tool | Kind | What it does |
|------|------|--------------|
| `stepper_state(wait_seconds)` | Tool | On or off, paused, the rules, whether an agent paused it, and the log held now (id, level, tag, first line). With `wait_seconds`, waits up to that long for a log to be held when none is. |
| `stepper_events`, `_get`, `_wait` | Feed | `Held: …` when the stepper holds a log and `Released: …` when it lets it go (kinds `Held`, `Released`), with the log's level, tag and `log_id`. |
| `stepper_pause(tags, level, auto_resume_seconds)` | Action | Turns the stepper on and holds at logs with these tags (comma-separated) at this level or above; every log when neither is given. `auto_resume_seconds` is one of the settings' delays. |
| `stepper_next(wait_seconds)` | Action | Lets the held log go, waits up to `wait_seconds` (3 by default) for the next one, and names both. |
| `stepper_resume` | Action | Stops holding and lets everything go; the stepper stays on with its rules. |

A pause made by `stepper_pause` ends by itself when the link to the hub drops (the agent's session ended, or the hub
stopped), so an agent never leaves the app held. A pause made in the app is left alone. None of the actions logs
through Console, since such a log would wait behind the held one.

A checkout test, for example: `stepper_pause(tags = "Checkout")`, tap *Place order*, `stepper_state(wait_seconds = 5)`
→ held at `Validating cart`; read the screen; `stepper_next` → held at `Reserved 2 items`; `details` shows the order;
`stepper_resume`.

## Architecture

```
Console.notify ─► processMutex ─► processors ─► observers … Stepper.emit() ─(awaits)─► … later observers
                                                               │
                                          Stepper.state / config (StateFlow)
                                                               │
                         StepperOverlayViewModel ─► dock widget (summary, panel, quick action)
                         StepperCaughtViewModel  ─► Stepper tab
```

| Piece | Role |
|-------|------|
| `stepper/Stepper` | Process-wide `LogObserver` and intent handler. Holds `Config` and `State` (`steppedEvents`, `pendingLogs`, `blockedLogId`, `blockedTag`). |
| `Stepper.emit` | When enabled, paused and the event matches, it parks on a `CompletableDeferred` (with an optional `withTimeoutOrNull` for auto-resume). `next()`, resuming or disabling completes it. |
| `addon/StepperAddon` | Adds `Stepper` to `Console`, registers the settings section and the agent entries, starts `StepperAgentGuard`; contributes `StepperTab`, `StepperNavGraph` and `StepperDockWidget`. |
| `agent/*` | The agent entries (`StepperAgentEntries`), the `stepper_events` records (`StepperEvents`), the answers' text, and `StepperAgentGuard`, which ends an agent's pause when the hub disconnects. |
| `dock/*` | `StepperDockWidget` maps the state to `Idle` / `Active` / `Attention`; `StepperPanel` and `StepperQuickAction` dispatch `StepperIntent`s. |
| `view/caught`, `view/logdetail` | The tab's list and the detail of one held event. |

### What pausing holds

Console processes one event at a time under a mutex, calling observers in registration order. A parked `emit`
therefore holds the **whole pipeline**, not just its own view:

- observers registered before the stepper have already seen the event; those after it (the log list may be one of
  them, depending on install order) see it only once it is released;
- every later event queues behind the held one;
- callers wait too: `Console.notify` returns at once, `asyncNotify` suspends, and `blockingNotify` blocks its thread.
  The network integrations use the last two, so a held network log holds that request (a suspended Ktor call, a
  blocked OkHttp thread) until you step;
- except on the main thread: there `blockingNotify` queues the event like `notify` and returns, since blocking would
  also block **Step** and **Resume** and freeze the app. The event is still held and shown in its turn, but the code
  that sent it does not stop. To stop a UI change at a step, send its log with `asyncNotify` from a coroutine and
  change the UI after it.
