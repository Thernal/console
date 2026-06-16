# Console — consumer R8 / ProGuard rules (published into every console AAR)
#
# Intentionally minimal. Console requires NO -keep rules in consuming apps:
#
#   * Auto-init ContentProviders (LoggingAutoInit, ConsoleNetworkAutoInit, ...) are
#     declared in the merged AndroidManifest, so R8/AAPT keep them automatically.
#   * The public API (ConsoleProvider, Console, ConsoleSeal, LogObserver, ...) is kept
#     through normal reachability from the consuming app's own code.
#   * LogRendererRegistry keys on runtime kotlin.reflect.KClass tokens, which stay
#     consistent under obfuscation (no name-based lookup).
#
# Console internals — the observer/processor pipeline and the registries — are SAFE to
# shrink and obfuscate, and SHOULD be. After sealing (ConsoleSeal.seal()), full R8
# obfuscation in release raises the bar against reverse-engineering the registration
# seams. Do not add broad -keep rules here; that would defeat the point.
