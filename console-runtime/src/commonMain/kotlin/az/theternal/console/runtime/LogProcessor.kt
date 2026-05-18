package az.theternal.console.runtime

fun interface LogProcessor {
    fun process(log: Log): Log
}
