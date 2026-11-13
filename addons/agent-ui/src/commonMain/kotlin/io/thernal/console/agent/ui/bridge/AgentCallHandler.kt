@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.agent.ui.bridge

import io.thernal.console.agent.AgentRegistry
import io.thernal.console.agent.AgentTool
import io.thernal.console.agent.ConsoleAgent
import io.thernal.console.core.ConsoleInternalApi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

/** Runs one `call` from the hub against the registry and returns the `result` message. */
internal class AgentCallHandler(private val redactor: () -> AgentRedactor) {
    suspend fun handle(message: JsonObject): String? {
        val id = message["id"]?.jsonPrimitive?.longOrNull ?: return null
        val name = message["tool"]?.jsonPrimitive?.contentOrNull.orEmpty()
        val arguments = message["args"] as? JsonObject ?: JsonObject(emptyMap())
        val tool = AgentRegistry.entries.value.firstNotNullOfOrNull { entries ->
            entries.tools.firstOrNull { it.name == name }
        }
        val (isError, text) = when {
            tool == null -> true to "The app has no tool \"$name\""
            tool.isAction && !ConsoleAgent.config.value.enableActions -> true to ACTIONS_OFF
            else -> run(tool = tool, arguments = arguments)
        }
        return AgentMessages.result(id = id, isError = isError, text = redactor().redact(text))
    }

    private suspend fun run(
        tool: AgentTool,
        arguments: JsonObject,
    ): Pair<Boolean, String> {
        val call = AgentArguments.parse(params = tool.params, arguments = arguments).getOrElse { error ->
            return true to error.message.orEmpty()
        }
        return try {
            val text = withTimeoutOrNull(CALL_TIMEOUT_MILLIS) { tool.run(call) }
            if (text != null) false to text else true to "\"${tool.name}\" did not answer in $CALL_TIMEOUT_MILLIS ms"
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            true to "\"${tool.name}\" failed: ${error.message ?: error::class.simpleName}"
        }
    }

    private companion object {
        const val CALL_TIMEOUT_MILLIS = 15_000L
        const val ACTIONS_OFF = "Actions are off in this app. Turn on Console → Settings → Agent → Allow actions."
    }
}
