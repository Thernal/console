package io.thernal.console.agent.ui.bridge

import io.thernal.console.agent.AgentCall
import io.thernal.console.agent.AgentParam
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull

/** Checks a call's JSON arguments against the tool's params and fills in defaults. */
internal object AgentArguments {
    /** The validated call, or the message telling the agent what was wrong. */
    fun parse(
        params: List<AgentParam>,
        arguments: JsonObject,
    ): Result<AgentCall> {
        val values = mutableMapOf<String, Any?>()
        for (param in params) {
            val raw = (arguments[param.key] as? JsonPrimitive)?.takeUnless { it.contentOrNull == null }
            val value = if (raw == null) {
                if (param.isRequired) return failure("Missing required argument \"${param.key}\"")
                param.defaultValue()
            } else {
                read(param, raw) ?: return failure(invalid(param))
            }
            values[param.key] = value
        }
        return Result.success(AgentCall(values))
    }

    private fun read(
        param: AgentParam,
        raw: JsonPrimitive,
    ): Any? {
        return when (param) {
            is AgentParam.Text -> raw.content

            is AgentParam.Integer -> (raw.intOrNull ?: raw.content.trim().toIntOrNull())?.takeIf {
                it in
                    param.min..param.max
            }

            is AgentParam.Toggle -> raw.booleanOrNull ?: raw.content.trim().toBooleanStrictOrNull()

            is AgentParam.Choice -> param.options.firstOrNull { it.equals(raw.content.trim(), ignoreCase = true) }
        }
    }

    private fun AgentParam.defaultValue(): Any? {
        return when (this) {
            is AgentParam.Text -> default
            is AgentParam.Integer -> default
            is AgentParam.Toggle -> default
            is AgentParam.Choice -> default
        }
    }

    private fun invalid(param: AgentParam): String {
        val expected = when (param) {
            is AgentParam.Text -> "text"
            is AgentParam.Integer -> "an integer in ${param.min}..${param.max}"
            is AgentParam.Toggle -> "true or false"
            is AgentParam.Choice -> "one of ${param.options.joinToString()}"
        }
        return "Argument \"${param.key}\" must be $expected"
    }

    private fun failure(message: String): Result<AgentCall> {
        return Result.failure(IllegalArgumentException(message))
    }
}
