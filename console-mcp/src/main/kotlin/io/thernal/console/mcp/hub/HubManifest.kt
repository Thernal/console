package io.thernal.console.mcp.hub

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/** What an app shares (`type: manifest`): its addons' tools and feeds. Actions are only listed while allowed. */
@Serializable
data class HubManifest(
    val actions: Boolean = false,
    val sections: List<Section> = emptyList(),
) {
    val tools: List<Tool> get() = sections.flatMap { it.tools }
    val feeds: List<Feed> get() = sections.flatMap { it.feeds }

    @Serializable
    data class Section(
        val id: String,
        val title: String,
        val about: String,
        val tools: List<Tool> = emptyList(),
        val feeds: List<Feed> = emptyList(),
    )

    @Serializable
    data class Tool(
        val name: String,
        val description: String,
        val action: Boolean = false,
        val params: List<Param> = emptyList(),
    )

    @Serializable
    data class Param(
        val key: String,
        val type: String,
        val description: String,
        val required: Boolean = false,
        val default: JsonElement? = null,
        val options: List<String>? = null,
        val min: Int? = null,
        val max: Int? = null,
    )

    @Serializable
    data class Feed(
        val name: String,
        val description: String,
    )
}
