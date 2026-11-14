@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.agent.ui.bridge

import io.thernal.console.agent.AgentCall
import io.thernal.console.agent.AgentImage
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
        if (tool == null) return failure(id = id, text = "The app has no tool \"$name\"")
        if (tool.isAction && !ConsoleAgent.config.value.enableActions) return failure(id = id, text = ACTIONS_OFF)
        val call = AgentArguments.parse(params = tool.params, arguments = arguments).getOrElse { error ->
            return failure(id = id, text = error.message.orEmpty())
        }
        val (isError, text) = run(tool = tool, call = call)
        return result(
            id = id,
            isError = isError,
            text = redactor().redact(text),
            images = if (isError) emptyList() else call.images,
        )
    }

    private suspend fun run(
        tool: AgentTool,
        call: AgentCall,
    ): Pair<Boolean, String> {
        return try {
            val text = withTimeoutOrNull(CALL_TIMEOUT_MILLIS) { tool.run(call) }
            if (text != null) false to text else true to "\"${tool.name}\" did not answer in $CALL_TIMEOUT_MILLIS ms"
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            true to "\"${tool.name}\" failed: ${error.message ?: error::class.simpleName}"
        }
    }

    private fun failure(
        id: Long,
        text: String,
    ): String {
        return AgentMessages.result(id = id, isError = true, text = redactor().redact(text))
    }

    /** The `result` message; images too large for one frame are left out, and the text says so. */
    private fun result(
        id: Long,
        isError: Boolean,
        text: String,
        images: List<AgentImage>,
    ): String {
        val message = AgentMessages.result(id = id, isError = isError, text = text, images = images)
        if (images.isEmpty() || message.encodeToByteArray().size <= MAX_FRAME_BYTES) return message
        val kilobytes = images.sumOf { it.bytes.size } / BYTES_PER_KILOBYTE
        val note = "\nThe image ($kilobytes KB) is too large for a message to the hub, so it was left out."
        return AgentMessages.result(id = id, isError = isError, text = text + note)
    }

    private companion object {
        const val CALL_TIMEOUT_MILLIS = 15_000L
        const val BYTES_PER_KILOBYTE = 1_024
        const val ACTIONS_OFF = "Actions are off in this app. Turn on Console → Settings → Agent → Allow actions."
    }
}
