package io.thernal.console.mcp.tools

import io.thernal.console.mcp.hub.DeviceStore

private const val BASE = "Console connects you to running apps (Android, iOS, JVM) that include the Console agent " +
    "bridge. Each app shares what its Console addons offer: feeds you can list, read in full and wait on " +
    "(`<feed>`, `<feed>_get`, `<feed>_wait`), and tools that read the app's state. Actions change the app and only " +
    "appear when the app allows them. Call `devices` to see the connected apps; pass `device` when there are " +
    "several. After changing code and relaunching the app, read feeds with `after` or wait with `<feed>_wait`."

/** Server instructions: the general usage plus one line per section the connected apps share. */
fun instructions(store: DeviceStore): String {
    val sections = store.devices()
        .flatMap { it.manifest.sections }
        .distinctBy { it.id }
        .joinToString(separator = "\n") { "- ${it.title}: ${it.about}" }
    return if (sections.isEmpty()) BASE else "$BASE\n\nWhat the connected apps share:\n$sections"
}
