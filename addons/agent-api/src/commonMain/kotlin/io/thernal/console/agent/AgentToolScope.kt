package io.thernal.console.agent

/**
 * Declares a tool's arguments and its body. Each argument function returns a typed handle; read it in [run] with
 * `call[handle]`. A text without a default is required; `optional…` arguments read as null when the agent omits them.
 */
interface AgentToolScope {
    fun text(
        key: String,
        description: String,
    ): AgentArg<String>

    fun text(
        key: String,
        description: String,
        default: String,
    ): AgentArg<String>

    fun optionalText(
        key: String,
        description: String,
    ): AgentArg<String?>

    fun int(
        key: String,
        description: String,
        default: Int,
        min: Int = Int.MIN_VALUE,
        max: Int = Int.MAX_VALUE,
    ): AgentArg<Int>

    fun optionalInt(
        key: String,
        description: String,
        min: Int = Int.MIN_VALUE,
        max: Int = Int.MAX_VALUE,
    ): AgentArg<Int?>

    fun toggle(
        key: String,
        description: String,
        default: Boolean = false,
    ): AgentArg<Boolean>

    fun choice(
        key: String,
        description: String,
        options: List<String>,
        default: String,
    ): AgentArg<String>

    fun optionalChoice(
        key: String,
        description: String,
        options: List<String>,
    ): AgentArg<String?>

    /** The tool body; returns the text the agent receives. Keep it compact: one line per item. */
    fun run(block: suspend (AgentCall) -> String)
}

/** A [AgentToolScope.choice] over the constants of [E]. */
inline fun <reified E : Enum<E>> AgentToolScope.enum(
    key: String,
    description: String,
    default: E,
): AgentArg<E> {
    return choice(
        key = key,
        description = description,
        options = enumValues<E>().map { it.name },
        default = default.name,
    ).map { name -> enumValueOf<E>(name) }
}
