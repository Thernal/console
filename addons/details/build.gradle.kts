plugins {
    alias(libs.plugins.convention.lib.core)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(libs.kotlinx.coroutines.core)
            implementation(projects.core.core)
        }
    }
}
