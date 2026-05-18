package az.theternal.console

fun interface LogProcessor {
    fun process(log: Log): Log
}
