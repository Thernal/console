plugins {
    kotlin("jvm")
    application
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.consoleRuntime)
    implementation(projects.consoleApi)
    implementation(projects.addons.agentUi)
    implementation(libs.kotlinx.coroutines.core)
}

application {
    mainClass.set("io.thernal.console.sample.agent.MainKt")
}
