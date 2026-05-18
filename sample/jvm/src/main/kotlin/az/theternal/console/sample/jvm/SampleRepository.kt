package az.theternal.console.sample.jvm

import az.theternal.console.Console
import az.theternal.console.Log

class SampleRepository {
    fun jvmLog() {
        Console.notify { Log(message = "JVM log") }
    }
}
