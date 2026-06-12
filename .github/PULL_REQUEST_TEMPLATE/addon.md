## Summary

<!-- What the addon does. -->

## Related issue

Closes #

## Modules

<!-- List the modules added, e.g. addons/my-addon-core, my-addon-core-noop, my-addon-ui -->

## Screenshots / recording

<!-- The addon's UI inside the console. -->

## Platforms verified

- [ ] Android
- [ ] iOS
- [ ] JVM (desktop)

## Checklist

- [ ] Follows the addon structure in `CONTRIBUTING.md` (`-core` / `-core-noop` / `-ui`)
- [ ] Uses the right convention plugins (`convention.lib.core` / `convention.lib.ui` + `convention.publish`)
- [ ] `LogRenderer` registered via `LogRendererRegistry` in `onInstall`
- [ ] Auto-init wired (Android `ConsoleAutoInitProvider` + manifest provider; iOS `consoleAddonInit`)
- [ ] No-op stub provided with the same public API
- [ ] `./gradlew assemble` passes
- [ ] `./gradlew detekt` passes
