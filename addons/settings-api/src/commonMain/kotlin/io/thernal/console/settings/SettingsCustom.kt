package io.thernal.console.settings

import androidx.compose.foundation.lazy.LazyListScope

/** Builds a [SettingsSection.Custom] escape-hatch section — see its KDoc for the tradeoffs. */
fun settingsCustom(
    id: String,
    title: String,
    order: Int = Int.MAX_VALUE,
    keywords: Set<String> = emptySet(),
    content: LazyListScope.() -> Unit,
): SettingsSection {
    return SettingsSection.Custom(
        id = id,
        title = title,
        order = order,
        keywords = keywords,
        content = content,
    )
}
