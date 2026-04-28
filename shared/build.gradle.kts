import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp-jvm-base")
    id("kmp-library-android")
    id("kmp-library-compose")
    id("kmp-library-test")
    id("koin-annotations")

//    alias(libs.plugins.kotzilla)
}

//kotzilla {
//    versionName = "1.0.0"
//}

kotlin {
    android {
        namespace = "org.example.calculator_hard.shared"
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
            export(libs.koin.core)
            export(project(":presentation"))
        }
    }

    jvm()

    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            api(projects.presentation)
            implementation(projects.domain)
            implementation(projects.data)

            implementation(libs.koin.compose)
        }
        commonTest.dependencies {
            implementation(libs.koin.test)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutinesSwing)
        }
    }
}
