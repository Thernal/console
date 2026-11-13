// The MCP SDK needs Ktor 3.5.x while the library modules pin Ktor 3.1.3 for consumers, so the desktop tool lives in
// its own build. It shares the repo's version catalog (like build-logic); its own libraries are the `mcp-` entries.
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

group = "io.github.thernal"
// Released with Console, under its version: the release attaches `console-mcp.zip` (README → Set up).
version = libs.versions.console.get()

kotlin {
    jvmToolchain(libs.versions.jvm.get().toInt())
}

dependencies {
    implementation(libs.mcp.kotlin.sdk.server)
    implementation(libs.mcp.ktor.network)
    implementation(libs.kotlin.logging.jvm)
    implementation(libs.slf4j.nop)
    testImplementation(libs.kotlin.test)
    testImplementation(libs.kotlinx.coroutines.test)
}

application {
    applicationName = "console-mcp"
    mainClass.set("io.thernal.console.mcp.MainKt")
}

// `console-mcp --version` and the MCP server info read the version from here.
tasks.jar {
    manifest {
        attributes("Implementation-Title" to "console-mcp", "Implementation-Version" to project.version)
    }
}

// The name a release attaches, with no version in it, so the latest release's link stays the same.
tasks.distZip {
    archiveFileName.set("console-mcp.zip")
}

tasks.test {
    useJUnitPlatform()
}
