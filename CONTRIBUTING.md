# Contributing to Console

## Getting started

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/Console.git`
3. Open in Android Studio (or IntelliJ IDEA)
4. Wait for Gradle sync to complete

## Branching

Branch off from `main`. Use the following naming convention:

| Type | Pattern | Example |
|------|---------|---------|
| Feature | `feature/short-description` | `feature/log-filtering` |
| Bug fix | `fix/short-description` | `fix/overlay-crash` |
| New addon | `addon/name` | `addon/network-inspector` |
| Docs / chore | `chore/short-description` | `chore/update-deps` |

## Commit messages

Follow [Conventional Commits](https://www.conventionalcommits.org):

```
<type>: <short summary>
```

Types: `feat`, `fix`, `refactor`, `docs`, `chore`, `test`

Examples:
```
feat: add log level filter to console view
fix: overlay not dismissed on back press
chore: update Kotlin to 2.1.0
```

## Code style

This project uses **ktlint** via Detekt. The pre-commit hook runs automatically after cloning (configured via `.githooks`).

To check manually:
```bash
./gradlew detekt          # check
./gradlew detektFormat    # auto-fix
```

## Project structure

```
console-runtime/        # Core data models and log processing
console-api/            # Public contracts (interfaces, sealed classes)
console-compose/        # Default Compose UI implementation
console-compose-noop/   # No-op implementation for release builds
designsystem/
  foundation/           # Colors, typography, shapes, theme
  components/           # Reusable Ds* composables
addons/                 # Optional feature modules
  details-api/          # Addon contract
  details-compose/      # Addon Compose implementation
  stepper-compose/      # Stepper addon
sample/                 # Sample app (not published)
```

## Adding a new addon

Addons are self-contained optional modules under `addons/`. Each addon typically consists of:

- `addons/<name>-api/` — Pure Kotlin contracts (no Compose, no UI)
- `addons/<name>-api-noop/` — No-op implementation for release builds
- `addons/<name>-compose/` — Compose UI implementation

### Steps

**1. Create the module directories**

```
addons/
  my-addon-api/
    build.gradle.kts
    src/commonMain/kotlin/io/thernal/console/myaddon/
  my-addon-api-noop/
    build.gradle.kts
    src/commonMain/kotlin/io/thernal/console/myaddon/
  my-addon-compose/
    build.gradle.kts
    src/commonMain/kotlin/io/thernal/console/myaddon/compose/
```

Modules are auto-discovered — no need to edit `settings.gradle.kts`.

**2. Use the correct convention plugins**

For API / noop modules (no Compose):
```kotlin
plugins {
    alias(libs.plugins.convention.lib.core)
    alias(libs.plugins.convention.publish)
}
```

For Compose modules:
```kotlin
plugins {
    alias(libs.plugins.convention.lib.ui)
    alias(libs.plugins.convention.publish)
}
```

**3. Package naming**

Follow the pattern: `io.thernal.console.<addonname>[.compose]`

**4. Implement `ConsoleAddon`**

```kotlin
class MyAddon : ConsoleAddon {
    override fun tab(): ConsoleTab = MyAddonTab()
    override fun navGraph(): ConsoleNavGraph = MyAddonNavGraph()
}
```

**5. Register in the Compose module's `AndroidManifest.xml`** (Android auto-init)

```xml
<provider
    android:name="io.thernal.console.myaddon.compose.MyAddonAutoInit"
    android:authorities="${applicationId}.my_addon_init"
    android:exported="false" />
```

## Pull requests

- Keep PRs focused — one feature or fix per PR
- Fill in the PR template
- All CI checks must pass before review
- New public API should include a usage example in the PR description

## What not to contribute

- Changes to `sample/` app that aren't tied to a library feature
- New dependencies without prior discussion in an issue
- Breaking changes to public API without a migration path
