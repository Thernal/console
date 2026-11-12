@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.details.ui.addon

import io.thernal.console.agent.AgentRegistry
import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.details.ui.agent.detailsAgentEntries
import io.thernal.console.details.ui.navigation.DetailsTab

object ConsoleDetailsAddon : ConsoleAddon {
    override fun onInstall() {
        AgentRegistry.register(detailsAgentEntries())
    }

    override fun tab(): ConsoleTab = DetailsTab
}
