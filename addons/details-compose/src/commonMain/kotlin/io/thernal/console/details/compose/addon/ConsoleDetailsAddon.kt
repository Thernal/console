package io.thernal.console.details.compose.addon

import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.details.compose.navigation.DetailsTab

object ConsoleDetailsAddon : ConsoleAddon {
    override fun tab(): ConsoleTab = DetailsTab
}
