package az.theternal.console.sample.jvm

import az.theternal.console.runtime.Console
import az.theternal.console.runtime.Log

class SampleRepository {
    fun jvmLog() {
        Console.notify { Log(message = "JVM log") }
    }
}
