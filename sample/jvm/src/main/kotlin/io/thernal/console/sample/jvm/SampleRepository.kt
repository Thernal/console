package io.thernal.console.sample.jvm

import io.thernal.console.runtime.console.Console
import io.thernal.console.core.log.Log

class SampleRepository {
    fun jvmLog() {
        Console.notify { Log(message = "JVM log") }
    }
}
