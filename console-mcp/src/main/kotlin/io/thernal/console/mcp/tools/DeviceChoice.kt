package io.thernal.console.mcp.tools

import io.thernal.console.mcp.hub.HubDevice

/** Result of resolving a tool's `device` argument. */
sealed interface DeviceChoice {
    data class Found(val device: HubDevice) : DeviceChoice

    data class Problem(val message: String) : DeviceChoice

    companion object {
        private const val NO_DEVICES = "No app has connected to the hub yet. Run a debug build that includes " +
            "console-agent-ui; Android devices also need `adb reverse tcp:<port> tcp:<port>`."

        /**
         * Picks among the devices that [offer] what the tool needs: by id or by a case-insensitive part of the name;
         * with no argument, the only connected one (or the only one at all).
         */
        fun resolve(
            devices: List<HubDevice>,
            argument: String?,
            offer: (HubDevice) -> Boolean,
        ): DeviceChoice {
            val offering = devices.filter(offer)
            if (offering.isEmpty()) {
                return Problem(
                    if (devices.isEmpty()) NO_DEVICES else "No connected app offers this.\n${describe(devices)}",
                )
            }
            val matches = if (argument.isNullOrBlank()) {
                offering.filter { it.connected }.ifEmpty { offering }
            } else {
                offering.filter { it.info.id == argument }
                    .ifEmpty {
                        offering.filter {
                            it.info.name.contains(
                                argument,
                                ignoreCase = true,
                            )
                        }.preferConnected()
                    }
            }
            return when (matches.size) {
                1 -> Found(matches.single())
                0 -> Problem("No device matches \"$argument\".\n${describe(offering)}")
                else -> Problem("Several devices match; pass `device` (an id or part of a name).\n${describe(matches)}")
            }
        }

        /** A name can match an app that left and one that is still here; the connected ones win. */
        private fun List<HubDevice>.preferConnected(): List<HubDevice> {
            return filter { it.connected }.ifEmpty { this }
        }

        fun describe(devices: List<HubDevice>): String {
            return devices.joinToString(separator = "\n") { device ->
                val info = device.info
                val state = if (device.connected) "connected" else "disconnected"
                val offers = (device.manifest.feeds.map { "${it.name} (feed)" } + device.manifest.tools.map { it.name })
                    .joinToString().ifEmpty { "nothing yet" }
                val actions = if (device.manifest.actions) "on" else "off"
                val app = info.app ?: "unknown app"
                val runs = if (device.runs > 1) " · run ${device.runs} (restarted)" else ""
                val detail = device.problem?.let { "not usable: $it" } ?: "offers: $offers · actions $actions"
                "- ${info.name} · ${info.platform} ${info.os} · $app · $state$runs · id ${info.id}\n  $detail"
            }
        }
    }
}
