@file:OptIn(ConsoleInternalApi::class)

package io.thernal.console.crash.ui.addon

import io.thernal.console.api.addon.ConsoleAddon
import io.thernal.console.api.addon.ConsoleNavGraph
import io.thernal.console.api.addon.ConsoleTab
import io.thernal.console.core.ConsoleInternalApi
import io.thernal.console.crash.LogCodecRegistry
import io.thernal.console.crash.ui.codec.NetworkRequestLogCodec
import io.thernal.console.crash.ui.codec.NetworkResponseLogCodec
import io.thernal.console.crash.ui.handler.CrashCapture
import io.thernal.console.crash.ui.handler.installCrashHandler
import io.thernal.console.crash.ui.lifecycle.installLifecycleTracking
import io.thernal.console.crash.ui.navigation.CrashReportNavGraph
import io.thernal.console.crash.ui.navigation.CrashReportTab
import io.thernal.console.crash.ui.runtime.CrashReportRuntime
import io.thernal.console.crash.ui.session.TerminationState
import io.thernal.console.crash.ui.writer.CrashStreamWriter
import io.thernal.console.network.NetworkLog
import io.thernal.console.runtime.console.Console
import kotlin.concurrent.Volatile

object CrashReportAddon : ConsoleAddon {

    // Guards the process-global side effects (handler chain, lifecycle callbacks) against a
    // second install; re-chaining the crash handler onto itself would loop at crash time.
    @Volatile
    private var isInstalled = false

    override fun onInstall() {
        if (isInstalled) return
        isInstalled = true

        CrashReportRuntime.start()
        Console.addObserver(CrashStreamWriter)
        registerNetworkCodecs()
        installCrashHandler(CrashCapture::onUncaught)
        installLifecycleTracking(::onLifecycleStateChanged)
    }

    override fun tab(): ConsoleTab = CrashReportTab

    override fun navGraph(): ConsoleNavGraph = CrashReportNavGraph

    private fun registerNetworkCodecs() {
        LogCodecRegistry.register(
            type = NetworkLog.Request::class,
            discriminator = NetworkRequestLogCodec.DISCRIMINATOR,
            codec = NetworkRequestLogCodec,
        )
        LogCodecRegistry.register(
            type = NetworkLog.Response::class,
            discriminator = NetworkResponseLogCodec.DISCRIMINATOR,
            codec = NetworkResponseLogCodec,
        )
    }

    private fun onLifecycleStateChanged(state: TerminationState) {
        CrashReportRuntime.store?.writeStateSidecar(state.name)
    }
}
