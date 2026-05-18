package az.theternal.console.addon.api.nav

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey

@Immutable
sealed interface ConsoleRoute : NavKey {

    @Immutable
    data object Stub : ConsoleRoute

    @Immutable
    data object Main : ConsoleRoute

    @Immutable
    data class LogDetail(
        val groupId: String,
        val logId: String,
    ) : ConsoleRoute
}
