import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp-jvm-base")
    id("kmp-library-android")
    id("kmp-library-compose")
    id("kmp-library-test")
}

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
            export(project(":presentation"))

            export(libs.decompose.core)
            export(libs.essenty.lifecycle)
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


            implementation(project.dependencies.platform(libs.koin.bom))
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
