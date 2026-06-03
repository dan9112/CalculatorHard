import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
}

fun getVersionFromGit(): String =
    try {
        val rawTag =
            providers
                .exec {
                    commandLine(
                        "git",
                        "describe",
                        "--tags",
                        "--abbrev=0",
                        "--first-parent",
                    )
                }.standardOutput
                .asText
                .get()
                .trim()

        rawTag
            .removePrefix("v")
            .ifEmpty { "0.0.1-SNAPSHOT" }
    } catch (_: Exception) {
        "0.0.1-SNAPSHOT" // Фоллбек, если git не установлен или репозиторий пустой
    }

kotlin {
    jvmToolchain(21)
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

android {
    namespace = "org.example.calculator"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()

    defaultConfig {
        applicationId = "org.example.calculator"
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        targetSdk =
            libs.versions.android.targetSdk
                .get()
                .toInt()
        versionCode = 1
        versionName = getVersionFromGit()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.foundation)
    implementation(libs.androidx.appcompat)
    implementation(libs.compose.uiToolingPreview)
    debugImplementation(libs.compose.uiTooling)
}
