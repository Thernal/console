package io.thernal.console.agent

/** No-op stand-in for the real [AgentArg]: opaque, since no tool ever runs. */
class AgentArg<T> internal constructor() {
    @Suppress("UnusedParameter")
    fun <R> map(transform: (T) -> R): AgentArg<R> {
        return AgentArg()
    }
}
