package io.thernal.console.sample.platform

actual fun platformLabel(): String {
    return "Desktop · JVM ${System.getProperty("java.version")}"
}

actual fun deviceModel(): String {
    return "${System.getProperty("os.name")} ${System.getProperty("os.version")} · ${System.getProperty("os.arch")}"
}
