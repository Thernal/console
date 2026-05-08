package az.theternal.console.sample.jvm

import az.theternal.console.core.Console
import az.theternal.console.core.base.Log

class SampleRepository {
    fun jvmLog() {
        Console.notify { Log(message = "JVM log") }
    }
}
