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
| `addon/StepperAddon` | Adds `Stepper` to `Console`, registers the settings section; contributes `StepperTab`, `StepperNavGraph` and `StepperDockWidget`. |
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
  blocked OkHttp thread) until you step.
