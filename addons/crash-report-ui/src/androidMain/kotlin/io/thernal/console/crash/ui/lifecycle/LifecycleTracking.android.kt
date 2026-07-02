package io.thernal.console.crash.ui.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import io.thernal.console.crash.ui.CrashReportContextHolder
import io.thernal.console.crash.ui.session.TerminationState

internal actual fun installLifecycleTracking(onStateChanged: (TerminationState) -> Unit) {
    val application = CrashReportContextHolder.applicationContext as? Application ?: return
    application.registerActivityLifecycleCallbacks(StartedCountCallbacks(onStateChanged))
}

private class StartedCountCallbacks(
    private val onStateChanged: (TerminationState) -> Unit,
) : Application.ActivityLifecycleCallbacks {

    private var startedCount = 0

    override fun onActivityStarted(activity: Activity) {
        startedCount += 1
        if (startedCount == 1) onStateChanged(TerminationState.FOREGROUND)
    }

    override fun onActivityStopped(activity: Activity) {
        startedCount -= 1
        if (startedCount == 0) onStateChanged(TerminationState.BACKGROUND)
    }

    override fun onActivityCreated(
        activity: Activity,
        savedInstanceState: Bundle?,
    ) = Unit

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivitySaveInstanceState(
        activity: Activity,
        outState: Bundle,
    ) = Unit

    override fun onActivityDestroyed(activity: Activity) = Unit
}
