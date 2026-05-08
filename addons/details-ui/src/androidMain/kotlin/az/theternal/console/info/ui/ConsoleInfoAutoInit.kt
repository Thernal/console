package az.theternal.console.info.ui

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import az.theternal.console.ui.ConsoleNavigation

internal class ConsoleInfoAutoInit : ContentProvider() {
    override fun onCreate(): Boolean {
        ConsoleNavigation.register(InfoNavGraph)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null
    override fun getType(uri: Uri): String? {
        return null
    }
    override fun insert(
        uri: Uri,
        values: ContentValues?,
    ): Uri? = null
    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0
}
