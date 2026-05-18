package az.theternal.console.sample.jvm

import az.theternal.console.api.Console
import az.theternal.console.model.Log

class SampleRepository {
    fun jvmLog() {
        Console.notify { Log(message = "JVM log") }
    }
}
