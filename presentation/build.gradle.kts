import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp-jvm-base")
    id("kmp-library-android")
    id("kmp-library-compose")
    id("kmp-library-test")
//    id("koin-annotations")
}

kotlin {
    android {
        namespace = "org.example.calculator.presentation"

        withHostTest { }
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
            implementation(projects.domain)

            implementation(libs.compose.components.resources)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.compose)

            api(libs.decompose.core)
            implementation(libs.essenty.lifecycle)
            implementation(libs.essenty.lifecycle.coroutines)
            api(libs.decompose.compose)

            implementation(libs.material.icons.extended)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutinesSwing)
        }
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
        }
    }
}

// koinCompiler {
//    compileSafety = false
// }
