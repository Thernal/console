@file:OptIn(ExperimentalForeignApi::class)

package io.thernal.console.crash.ui.store

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSData
import platform.Foundation.NSFileHandle
import platform.Foundation.NSFileManager
import platform.Foundation.NSNumber
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSURLIsExcludedFromBackupKey
import platform.Foundation.NSUserDomainMask
import platform.Foundation.closeFile
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.fileHandleForWritingAtPath
import platform.Foundation.seekToEndOfFile
import platform.Foundation.writeToFile
import platform.posix.memcpy

private const val DIRECTORY_NAME = "console-crash-report"

internal actual object CrashFileSystem {

    actual fun defaultBaseDirectoryPath(): String? {
        val supportDirectory = NSSearchPathForDirectoriesInDomains(
            directory = NSApplicationSupportDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true,
        ).firstOrNull() as? String ?: return null
        val path = "$supportDirectory/$DIRECTORY_NAME"
        if (!ensureDirectory(path)) return null
        excludeFromBackup(path)
        return path
    }

    actual fun temporaryDirectoryPath(): String {
        return NSTemporaryDirectory().trimEnd('/')
    }

    actual fun ensureDirectory(path: String): Boolean {
        val manager = NSFileManager.defaultManager
        if (manager.fileExistsAtPath(path)) return true
        return manager.createDirectoryAtPath(
            path = path,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )
    }

    actual fun listFileNames(directoryPath: String): List<String> {
        return NSFileManager.defaultManager
            .contentsOfDirectoryAtPath(directoryPath, error = null)
            ?.filterIsInstance<String>()
            .orEmpty()
    }

    actual fun openAppend(path: String): CrashAppendSink? {
        val manager = NSFileManager.defaultManager
        if (!manager.fileExistsAtPath(path)) {
            val isCreated = manager.createFileAtPath(path = path, contents = null, attributes = null)
            if (!isCreated) return null
        }
        val handle = NSFileHandle.fileHandleForWritingAtPath(path) ?: return null
        handle.seekToEndOfFile()
        return AppendSink(handle)
    }

    actual fun readBytes(path: String): ByteArray? {
        return NSData.dataWithContentsOfFile(path)?.toByteArray()
    }

    actual fun writeAtomically(
        path: String,
        bytes: ByteArray,
    ): Boolean {
        return bytes.toNSData().writeToFile(path, atomically = true)
    }

    actual fun rename(
        fromPath: String,
        toPath: String,
    ): Boolean {
        val manager = NSFileManager.defaultManager
        if (manager.fileExistsAtPath(toPath)) {
            manager.removeItemAtPath(toPath, error = null)
        }
        return manager.moveItemAtPath(srcPath = fromPath, toPath = toPath, error = null)
    }

    actual fun delete(path: String): Boolean {
        return NSFileManager.defaultManager.removeItemAtPath(path, error = null)
    }

    actual fun exists(path: String): Boolean {
        return NSFileManager.defaultManager.fileExistsAtPath(path)
    }

    private fun excludeFromBackup(path: String) {
        NSURL.fileURLWithPath(path).setResourceValue(
            value = NSNumber(bool = true),
            forKey = NSURLIsExcludedFromBackupKey,
            error = null,
        )
    }

    private class AppendSink(private val handle: NSFileHandle) : CrashAppendSink {

        // NSFileHandle writes are unbuffered write(2) syscalls, so each record reaches the kernel
        // immediately — surviving process death without an fsync. The error-returning overload is
        // used because the legacy one raises an ObjC exception Kotlin cannot catch.
        override fun append(bytes: ByteArray): Boolean {
            return handle.writeData(bytes.toNSData(), error = null)
        }

        override fun close() {
            handle.closeFile()
        }
    }
}

private fun ByteArray.toNSData(): NSData {
    if (isEmpty()) return NSData()
    return usePinned { pinned ->
        NSData.dataWithBytes(bytes = pinned.addressOf(0), length = size.toULong())
    }
}

private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    val result = ByteArray(size)
    result.usePinned { pinned ->
        memcpy(pinned.addressOf(0), bytes, length)
    }
    return result
}
