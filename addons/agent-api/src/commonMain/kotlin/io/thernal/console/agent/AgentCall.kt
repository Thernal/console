package io.thernal.console.agent

/**
 * Arguments of one tool call. The bridge validates them against the tool's [AgentParam]s and fills in defaults
 * before the tool runs, so [get] returns a value of the declared type.
 */
class AgentCall(private val values: Map<String, Any?>) {
    operator fun <T> get(arg: AgentArg<T>): T {
        return arg.read(values[arg.param.key])
    }
}
