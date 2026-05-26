plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "az.theternal.console.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "az.theternal.console.sample"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") { dimension = "environment" }
        create("prod") { dimension = "environment" }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Prod flavor swaps full console implementations for their noop counterparts.
configurations.matching { it.name.startsWith("prod") }.configureEach {
    resolutionStrategy.dependencySubstitution {
        substitute(project(":console-compose")).using(project(":console-compose-noop"))
        substitute(project(":addons:details-api")).using(project(":addons:details-api-noop"))
    }
}

dependencies {
    implementation(projects.sample.app)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(projects.sample.jvm)
}
