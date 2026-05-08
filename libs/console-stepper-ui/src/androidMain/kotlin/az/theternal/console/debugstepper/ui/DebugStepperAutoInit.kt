package az.theternal.console.debugstepper.ui

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import az.theternal.console.core.Console
import az.theternal.console.debugstepper.DebugStepper
import az.theternal.console.debugstepper.ui.overlay.DebugStepperOverlay
import az.theternal.console.ui.ConsoleNavigation
import az.theternal.console.ui.ConsoleOverlays

internal class DebugStepperAutoInit : ContentProvider() {
    override fun onCreate(): Boolean {
        Console.addObserver(DebugStepper)
        ConsoleNavigation.register(DebugStepperNavGraph)
        ConsoleOverlays.register { DebugStepperOverlay() }
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
