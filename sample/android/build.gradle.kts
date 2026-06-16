plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "io.thernal.console.sample"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.thernal.console.sample"
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

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // Sign the minified build with the debug key so it can be installed locally
            // for R8 smoke-testing. Not for distribution.
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

// Prod flavor swaps full console implementations for their noop counterparts.
configurations.matching { it.name.startsWith("prod") }.configureEach {
    resolutionStrategy.dependencySubstitution {
        substitute(module("io.github.thernal:console-ui")).using(module("io.github.thernal:console-ui-noop:0.1.0"))
        substitute(module("io.github.thernal:console-details-core")).using(module("io.github.thernal:console-details-core-noop:0.1.0"))
    }
}

dependencies {
    implementation(projects.sample.app)
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.android)
    implementation(projects.sample.jvm)
}
