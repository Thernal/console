package io.thernal.console.agent.ui.device

private const val OS_NAME = "os.name"
private const val OS_VERSION = "os.version"
private const val JAVA_COMMAND = "sun.java.command"

internal actual fun readAgentDevice(runId: String): AgentDevice {
    val osName = System.getProperty(OS_NAME).orEmpty()
    return AgentDevice(
        id = runId,
        runId = runId,
        name = "Desktop ($osName)",
        platform = "JVM",
        osVersion = System.getProperty(OS_VERSION).orEmpty(),
        appId = System.getProperty(JAVA_COMMAND)?.substringBefore(' ')?.ifEmpty { null },
    )
}
