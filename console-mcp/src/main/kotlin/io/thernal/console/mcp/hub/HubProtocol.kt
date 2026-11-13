package io.thernal.console.mcp.hub

/**
 * The versions of the messages between an app and the hub. An app names its own in `hello` (`PROTOCOL_VERSION` in
 * `console-agent-ui`); the number goes up only when the messages change, not with every Console release, so a hub
 * reads the apps of the releases around its own.
 */
object HubProtocol {
    /** The version this hub speaks. */
    const val CURRENT = 2

    /** The oldest version this hub still reads. */
    const val OLDEST = 2

    /** Why an app that speaks [version] cannot be used, with what to update; null when the hub reads it. */
    fun problem(version: Int?): String? {
        return when {
            version == null -> "the app sent no protocol version; update Console in the app"

            version > CURRENT ->
                "the app speaks protocol $version and this console-mcp $CURRENT; install the console-mcp of the " +
                    "Console release the app uses"

            version < OLDEST ->
                "the app speaks protocol $version and this console-mcp reads $OLDEST to $CURRENT; update Console " +
                    "in the app, or install the console-mcp of its release"

            else -> null
        }
    }
}
