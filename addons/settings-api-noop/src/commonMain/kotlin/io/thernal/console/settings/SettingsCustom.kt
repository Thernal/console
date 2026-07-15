package io.thernal.console.settings

import androidx.compose.foundation.lazy.LazyListScope

@Suppress("UnusedParameter")
fun settingsCustom(
    id: String,
    title: String,
    order: Int = Int.MAX_VALUE,
    keywords: Set<String> = emptySet(),
    content: LazyListScope.() -> Unit,
): SettingsSection = SettingsSection()
