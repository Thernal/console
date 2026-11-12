package io.thernal.console.agent

/** Same DSL receiver as `agent-api`, kept so a tool's `block` (never invoked in the no-op path) still type-checks. */
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
