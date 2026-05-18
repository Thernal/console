plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_23
    targetCompatibility = JavaVersion.VERSION_23
}

dependencies {
    implementation(projects.consoleRuntime)
}