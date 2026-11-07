pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    // Versions only; a plugin is resolved when a module applies it, so the Android
    // plugins are not needed to build :core.
    plugins {
        kotlin("jvm") version "2.1.20"
        id("com.android.application") version "8.9.1"
        id("org.jetbrains.kotlin.android") version "2.1.20"
        id("org.jetbrains.kotlin.plugin.compose") version "2.1.20"
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        google()
    }
}

rootProject.name = "hearo"

// :core is pure Kotlin/JVM (no Android) and always builds.
include(":core")

// :app needs the Android SDK; include it only when one is configured so that
// `./gradlew :core:test` also works on machines/CI without the SDK.
val hasAndroidSdk = System.getenv("ANDROID_HOME") != null ||
    System.getenv("ANDROID_SDK_ROOT") != null ||
    file("local.properties").let { it.exists() && it.readText().contains("sdk.dir") }
if (hasAndroidSdk) include(":app")
