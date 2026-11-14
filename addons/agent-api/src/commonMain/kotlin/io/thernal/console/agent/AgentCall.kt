package io.thernal.console.agent

import io.thernal.console.core.ConsoleInternalApi

/**
 * Arguments of one tool call. The bridge validates them against the tool's [AgentParam]s and fills in defaults
 * before the tool runs, so [get] returns a value of the declared type.
 */
class AgentCall(private val values: Map<String, Any?>) {
    private val attached = mutableListOf<AgentImage>()

    /** The images [attach] added, in order; the bridge sends them with the text the tool returns. */
    @ConsoleInternalApi
    val images: List<AgentImage>
        get() = attached.toList()

    operator fun <T> get(arg: AgentArg<T>): T {
        return arg.read(values[arg.param.key])
    }

    /** Adds [image] to this call's answer, beside the text the tool's `run` body returns. */
    fun attach(image: AgentImage) {
        attached += image
    }
}
