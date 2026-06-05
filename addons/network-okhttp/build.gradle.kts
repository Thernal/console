plugins {
    alias(libs.plugins.convention.lib.core)
    alias(libs.plugins.convention.publish)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.addons.networkCore)
        }
        val jvmSharedMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.okhttp)
            }
        }
        androidMain { dependsOn(jvmSharedMain) }
        jvmMain { dependsOn(jvmSharedMain) }
    }
}
