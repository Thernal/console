package io.thernal.console.api.autoinit

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import io.thernal.console.io.ConsoleIoContextHolder

abstract class ConsoleAutoInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        // Android gives no init-order guarantee across providers merged from library manifests,
        // so every addon's provider captures the Context — whichever runs first makes file-backed
        // stores (settings overrides, crash sessions) resolvable before any addon's init() runs.
        context?.applicationContext?.let { ConsoleIoContextHolder.applicationContext = it }
        init()
        return true
    }

    protected abstract fun init()

    final override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null

    final override fun getType(uri: Uri): String? = null

    final override fun insert(
        uri: Uri,
        values: ContentValues?,
    ): Uri? = null

    final override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0

    final override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0
}
