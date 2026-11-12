package io.thernal.console.agent

/** Typed handle of a declared [AgentParam]; read its value inside the tool with `call[arg]`. */
class AgentArg<T> internal constructor(
    val param: AgentParam,
    private val convert: (Any?) -> T,
) {
    internal fun read(raw: Any?): T {
        return convert(raw)
    }

    /** Same argument, converted further, e.g. a choice name into an enum constant. */
    fun <R> map(transform: (T) -> R): AgentArg<R> {
        return AgentArg(param) { transform(convert(it)) }
    }
}
