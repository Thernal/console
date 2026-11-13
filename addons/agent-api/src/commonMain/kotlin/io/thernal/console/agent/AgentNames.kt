package io.thernal.console.agent

/** Naming rules shared by the DSL and the registry. */
internal object AgentNames {
    /** Tool and feed names: lower snake case, short enough for the `_wait` suffix the hub adds to feeds. */
    val NAME_PATTERN = Regex("^[a-z][a-z0-9_]{0,47}$")

    /** Argument keys: lower camel or snake case. */
    val KEY_PATTERN = Regex("^[a-z][A-Za-z0-9_]{0,47}$")

    /** Every hub tool takes this argument to pick the device, so a tool cannot declare it. */
    const val DEVICE_KEY = "device"

    /** Tools the hub provides itself. */
    val RESERVED = setOf("devices")

    const val FEED_GET_SUFFIX = "_get"
    const val FEED_WAIT_SUFFIX = "_wait"
}
