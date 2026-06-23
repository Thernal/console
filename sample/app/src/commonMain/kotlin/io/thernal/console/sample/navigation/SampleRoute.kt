package io.thernal.console.sample.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey

/** Navigation destinations of the demo host app. */
@Immutable
sealed interface SampleRoute : NavKey {
    @Immutable
    data object Home : SampleRoute

    @Immutable
    data object Counter : SampleRoute

    @Immutable
    data object Brew : SampleRoute

    @Immutable
    data object Playground : SampleRoute
}
