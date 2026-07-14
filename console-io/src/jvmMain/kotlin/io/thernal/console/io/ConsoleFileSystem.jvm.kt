package io.thernal.console.io

import java.io.File
import java.io.FileOutputStream

actual object ConsoleFileSystem {

    actual fun baseDirectoryPath(directoryName: String): String? {
        val home = System.getProperty("user.home")?.takeIf { it.isNotBlank() }
        val base = home?.let { File(it, ".console") } ?: File(temporaryDirectoryPath())
        return File(base, directoryName).absolutePath
    }

    actual fun temporaryDirectoryPath(): String {
        return System.getProperty("java.io.tmpdir")
    }

    actual fun ensureDirectory(path: String): Boolean {
        val directory = File(path)
        return directory.isDirectory || directory.mkdirs()
    }

    actual fun listFileNames(directoryPath: String): List<String> {
        return File(directoryPath).listFiles()?.map { it.name }.orEmpty()
    }

    actual fun openAppend(path: String): AppendSink? {
        return runCatching { JvmAppendSink(FileOutputStream(path, true)) }.getOrNull()
    }

    actual fun readBytes(path: String): ByteArray? {
        return runCatching { File(path).takeIf { it.isFile }?.readBytes() }.getOrNull()
    }

    actual fun writeAtomically(
        path: String,
        bytes: ByteArray,
    ): Boolean {
        return runCatching {
            val temp = File("$path.tmp")
            temp.writeBytes(bytes)
            replace(from = temp, to = File(path))
        }.getOrDefault(false)
    }

    actual fun rename(
        fromPath: String,
        toPath: String,
    ): Boolean {
        return runCatching { replace(from = File(fromPath), to = File(toPath)) }.getOrDefault(false)
    }

    actual fun delete(path: String): Boolean {
        return File(path).delete()
    }

    actual fun exists(path: String): Boolean {
        return File(path).exists()
    }

    private fun replace(
        from: File,
        to: File,
    ): Boolean {
        if (from.renameTo(to)) return true
        to.delete()
        return from.renameTo(to)
    }

    private class JvmAppendSink(private val stream: FileOutputStream) : AppendSink {

        // FileOutputStream.write is an unbuffered write(2) syscall, so each record reaches the
        // kernel page cache immediately — surviving process death without an fsync.
        override fun append(bytes: ByteArray): Boolean {
            return runCatching { stream.write(bytes) }.isSuccess
        }

        override fun close() {
            runCatching { stream.close() }
        }
    }
}
