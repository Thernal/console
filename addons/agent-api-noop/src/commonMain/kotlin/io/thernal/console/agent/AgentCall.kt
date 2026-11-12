package io.thernal.console.agent

/** No-op stand-in for the real [AgentCall]: tool bodies never run in the no-op path, so [get] is never reached. */
@Suppress("UnusedParameter", "UnusedPrivateProperty")
class AgentCall(values: Map<String, Any?>) {
    operator fun <T> get(arg: AgentArg<T>): T {
        throw UnsupportedOperationException("Agent tools never run in release")
    }
}
