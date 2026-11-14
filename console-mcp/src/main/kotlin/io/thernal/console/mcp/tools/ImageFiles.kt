package io.thernal.console.mcp.tools

import io.thernal.console.mcp.hub.HubImage
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64

/**
 * Saves the images apps answer with, so the agent can name a file the person opens: `<name>-<time>.png` in
 * [directory], by default `console-mcp/images` in the system's temporary directory, which the system clears over time.
 */
class ImageFiles(private val directory: Path = defaultDirectory()) {
    /** Writes [image] and returns its path. */
    fun save(image: HubImage): Result<Path> {
        return runCatching {
            Files.createDirectories(directory)
            val base = "${safeName(image.name)}-${LocalDateTime.now().format(TIME_FORMAT)}"
            val extension = EXTENSIONS[image.mime] ?: DEFAULT_EXTENSION
            val path = generateSequence(1) { it + 1 }
                .map { index -> directory.resolve(if (index == 1) "$base.$extension" else "$base-$index.$extension") }
                .first { !Files.exists(it) }
            Files.write(path, Base64.getDecoder().decode(image.data))
            path.toAbsolutePath()
        }
    }

    private fun safeName(name: String): String {
        return name.replace(UNSAFE, "_").take(MAX_NAME_LENGTH).ifEmpty { DEFAULT_NAME }
    }

    private companion object {
        const val MAX_NAME_LENGTH = 48
        const val DEFAULT_NAME = "image"
        const val DEFAULT_EXTENSION = "img"
        val UNSAFE = Regex("[^A-Za-z0-9_-]")
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")
        val EXTENSIONS = mapOf(
            "image/png" to "png",
            "image/jpeg" to "jpg",
            "image/gif" to "gif",
            "image/webp" to "webp",
        )

        fun defaultDirectory(): Path {
            return Paths.get(System.getProperty("java.io.tmpdir"), "console-mcp", "images")
        }
    }
}
