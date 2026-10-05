# console-settings-api-noop

Release stand-in for [`console-settings-api`](../settings-api/README.md): `settingsEntries`, `settingsCustom` and
`SettingsRegistry` keep their signatures, but nothing is registered and `SettingsRegistry.sections` stays empty. Code
that registers settings compiles unchanged in release builds.

```kotlin
releaseImplementation("io.github.thernal:console-settings-api-noop:<version>")
```
