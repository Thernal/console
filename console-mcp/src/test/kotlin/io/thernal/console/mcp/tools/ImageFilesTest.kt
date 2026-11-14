package io.thernal.console.mcp.tools

import io.thernal.console.mcp.hub.HubImage
import java.nio.file.Files
import java.util.Base64
import kotlin.io.path.name
import kotlin.io.path.readBytes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ImageFilesTest {
    private val directory = Files.createTempDirectory("image-files-test")
    private val files = ImageFiles(directory)

    @Test
    fun `an image is written with its bytes under its name`() {
        val bytes = byteArrayOf(1, 2, 3)

        val path = files.save(image(name = "ui_snapshot", bytes = bytes)).getOrThrow()

        assertEquals(directory.toAbsolutePath(), path.parent)
        assertTrue(path.name.startsWith("ui_snapshot-"))
        assertTrue(path.name.endsWith(".png"))
        assertTrue(bytes.contentEquals(path.readBytes()))
    }

    @Test
    fun `two images saved at once get two files`() {
        val first = files.save(image(name = "shot", bytes = byteArrayOf(1))).getOrThrow()
        val second = files.save(image(name = "shot", bytes = byteArrayOf(2))).getOrThrow()

        assertNotEquals(first, second)
    }

    @Test
    fun `a name cannot leave the directory`() {
        val path = files.save(image(name = "../../etc/passwd", bytes = byteArrayOf(1))).getOrThrow()

        assertEquals(directory.toAbsolutePath(), path.parent)
        assertTrue(path.name.startsWith("______etc_passwd-"))
    }

    @Test
    fun `data that is not base64 is reported, not thrown`() {
        val result = files.save(HubImage(name = "shot", mime = "image/png", data = "not base64!"))

        assertTrue(result.isFailure)
    }

    private fun image(
        name: String,
        bytes: ByteArray,
    ): HubImage {
        return HubImage(name = name, mime = "image/png", data = Base64.getEncoder().encodeToString(bytes))
    }
}
