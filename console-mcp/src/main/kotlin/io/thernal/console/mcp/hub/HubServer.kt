package io.thernal.console.mcp.hub

import io.ktor.network.selector.SelectorManager
import io.ktor.network.sockets.Socket
import io.ktor.network.sockets.aSocket
import io.ktor.network.sockets.openReadChannel
import io.ktor.network.sockets.openWriteChannel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

/**
 * The TCP side of the hub: apps with the agent bridge connect here, describe what they share and stream their feeds.
 * It listens on loopback only; USB devices reach it through `adb reverse`, simulators and desktop apps directly.
 */
class HubServer(
    private val store: DeviceStore,
    private val host: String,
    private val port: Int,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    @Volatile
    var status: HubStatus = HubStatus.Starting
        private set

    private val json = Json { ignoreUnknownKeys = true }

    suspend fun run() {
        val selector = SelectorManager(dispatcher)
        try {
            aSocket(selector).tcp().bind(hostname = host, port = port).use { server ->
                status = HubStatus.Listening("$host:$port")
                coroutineScope {
                    while (isActive) {
                        val socket = server.accept()
                        launch { serve(socket) }
                    }
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (error: Exception) {
            val failed = HubStatus.Failed("Cannot listen on $host:$port: ${error.message ?: error::class.simpleName}")
            status = failed
            System.err.println("console-mcp: ${failed.reason}")
        } finally {
            selector.close()
        }
    }

    private suspend fun serve(socket: Socket) {
        var deviceId: String? = null
        val link = HubLink(socket.openWriteChannel(autoFlush = false))
        try {
            socket.use {
                val input = it.openReadChannel()
                while (true) {
                    val message = json.parseToJsonElement(input.readFrame()).jsonObject
                    val address = it.remoteAddress.toString()
                    deviceId = handle(message = message, deviceId = deviceId, address = address, link = link)
                }
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (ignored: Exception) {
            // The app closed the connection or sent something unreadable.
        } finally {
            link.close()
            deviceId?.let { store.disconnect(deviceId = it, link = link) }
        }
    }

    /** Applies one message and returns the connection's device id (set by `hello`). */
    private fun handle(
        message: JsonObject,
        deviceId: String?,
        address: String,
        link: HubLink,
    ): String? {
        when (message["type"]?.jsonPrimitive?.contentOrNull) {
            "hello" -> {
                val info = json.decodeFromJsonElement(HubDeviceInfo.serializer(), message.getValue("device"))
                deviceId?.let { store.disconnect(deviceId = it, link = link) }
                store.connect(info = info, address = address, link = link)
                return info.id
            }

            "manifest" -> deviceId?.let {
                store.setManifest(
                    deviceId = it,
                    manifest = json.decodeFromJsonElement(HubManifest.serializer(), message),
                )
            }

            "record" -> deviceId?.let {
                store.addRecord(deviceId = it, record = json.decodeFromJsonElement(HubRecord.serializer(), message))
            }

            "result" -> {
                val id = message["id"]?.jsonPrimitive?.longOrNull ?: return deviceId
                val isError = message["error"]?.jsonPrimitive?.booleanOrNull ?: false
                val text = message["text"]?.jsonPrimitive?.contentOrNull.orEmpty()
                link.complete(id = id, result = HubCallResult(isError = isError, text = text))
            }
        }
        return deviceId
    }
}
