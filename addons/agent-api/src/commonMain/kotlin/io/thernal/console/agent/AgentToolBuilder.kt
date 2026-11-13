package io.thernal.console.agent

internal class AgentToolBuilder(private val name: String) : AgentToolScope {
    private val params = mutableListOf<AgentParam>()
    private var block: (suspend (AgentCall) -> String)? = null

    override fun text(
        key: String,
        description: String,
    ): AgentArg<String> {
        return add(AgentParam.Text(key = key, description = description, isRequired = true, default = null)) {
            it as String
        }
    }

    override fun text(
        key: String,
        description: String,
        default: String,
    ): AgentArg<String> {
        return add(AgentParam.Text(key = key, description = description, isRequired = false, default = default)) {
            it as String
        }
    }

    override fun optionalText(
        key: String,
        description: String,
    ): AgentArg<String?> {
        return add(AgentParam.Text(key = key, description = description, isRequired = false, default = null)) {
            it as String?
        }
    }

    override fun int(
        key: String,
        description: String,
        default: Int,
        min: Int,
        max: Int,
    ): AgentArg<Int> {
        require(default in min..max) { "Tool \"$name\": default of \"$key\" is outside $min..$max" }
        val param = AgentParam.Integer(
            key = key,
            description = description,
            isRequired = false,
            default = default,
            min = min,
            max = max,
        )
        return add(param) { it as Int }
    }

    override fun optionalInt(
        key: String,
        description: String,
        min: Int,
        max: Int,
    ): AgentArg<Int?> {
        val param = AgentParam.Integer(
            key = key,
            description = description,
            isRequired = false,
            default = null,
            min = min,
            max = max,
        )
        return add(param) { it as Int? }
    }

    override fun toggle(
        key: String,
        description: String,
        default: Boolean,
    ): AgentArg<Boolean> {
        return add(AgentParam.Toggle(key = key, description = description, default = default)) { it as Boolean }
    }

    override fun choice(
        key: String,
        description: String,
        options: List<String>,
        default: String,
    ): AgentArg<String> {
        require(default in options) { "Tool \"$name\": default of \"$key\" is not one of its options" }
        val param = AgentParam.Choice(
            key = key,
            description = description,
            isRequired = false,
            options = options,
            default = default,
        )
        return add(param) { it as String }
    }

    override fun optionalChoice(
        key: String,
        description: String,
        options: List<String>,
    ): AgentArg<String?> {
        val param = AgentParam.Choice(
            key = key,
            description = description,
            isRequired = false,
            options = options,
            default = null,
        )
        return add(param) { it as String? }
    }

    override fun run(block: suspend (AgentCall) -> String) {
        check(this.block == null) { "Tool \"$name\" has two run blocks" }
        this.block = block
    }

    fun build(
        description: String,
        isAction: Boolean,
    ): AgentTool {
        return AgentTool(
            name = name,
            description = description,
            params = params.toList(),
            isAction = isAction,
            block = checkNotNull(block) { "Tool \"$name\" has no run block" },
        )
    }

    private fun <T> add(
        param: AgentParam,
        convert: (Any?) -> T,
    ): AgentArg<T> {
        require(param.key.matches(AgentNames.KEY_PATTERN)) { "Tool \"$name\": invalid argument key \"${param.key}\"" }
        require(param.key != AgentNames.DEVICE_KEY) { "Tool \"$name\": \"${AgentNames.DEVICE_KEY}\" is reserved" }
        require(params.none { it.key == param.key }) { "Tool \"$name\" declares \"${param.key}\" twice" }
        params += param
        return AgentArg(param, convert)
    }
}
