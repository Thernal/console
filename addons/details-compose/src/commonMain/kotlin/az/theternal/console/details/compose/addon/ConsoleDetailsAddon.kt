package az.theternal.console.details.compose.addon

import az.theternal.console.api.addon.ConsoleAddon
import az.theternal.console.api.addon.ConsoleTab
import az.theternal.console.details.compose.navigation.DetailsTab

object ConsoleDetailsAddon : ConsoleAddon {
    override fun tab(): ConsoleTab = DetailsTab
}
