package io.thernal.console.crash.ui.lifecycle

import io.thernal.console.crash.ui.session.TerminationState
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationDidEnterBackgroundNotification

internal actual fun installLifecycleTracking(onStateChanged: (TerminationState) -> Unit) {
    val center = NSNotificationCenter.defaultCenter
    center.addObserverForName(
        name = UIApplicationDidBecomeActiveNotification,
        `object` = null,
        queue = null,
    ) { _ ->
        onStateChanged(TerminationState.FOREGROUND)
    }
    center.addObserverForName(
        name = UIApplicationDidEnterBackgroundNotification,
        `object` = null,
        queue = null,
    ) { _ ->
        onStateChanged(TerminationState.BACKGROUND)
    }
}
