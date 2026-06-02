package io.thernal.console.sample.jvm

import io.thernal.console.runtime.Console
import io.thernal.console.runtime.Log

class SampleRepository {
    fun jvmLog() {
        Console.notify { Log(message = "JVM log") }
    }
}
