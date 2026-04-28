@file:Suppress("UnstableApiUsage")

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
/*
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    // ✅ Версии плагинов берутся из каталога. Изменили в TOML → обновилось везде.
    plugins {
        id("org.jetbrains.kotlin.multiplatform") version libs.versions.kotlin.get() apply false
        id("com.android.kotlin.multiplatform.library") version libs.versions.agp.get() apply false
        id("org.jetbrains.compose") version libs.versions.compose.get() apply false
        id("org.jetbrains.kotlin.plugin.compose") version libs.versions.kotlin.get() apply false
    }
}*/

rootProject.name = "build-logic"
include(":convention")
