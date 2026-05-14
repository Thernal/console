package az.theternal.console.ui.autoinit

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

abstract class ConsoleAutoInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
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
