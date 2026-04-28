import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("com.android.kotlin.multiplatform.library")
}

kotlin {
    android {
        // namespace ОПУЩЕН: он уникален для каждого модуля, задаётся в самом модуле
        compileSdk = findCatalogRequiredVersion("android-compileSdk").toInt()
        minSdk = findCatalogRequiredVersion("android-minSdk").toInt()

        androidResources {
            enable = true
        }

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }
}

dependencies {
    "androidRuntimeClasspath"(findCatalogLibrary("compose-uiTooling"))
}
