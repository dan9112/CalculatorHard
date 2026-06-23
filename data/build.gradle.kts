import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp-jvm-base")
    id("kmp-library-android")
    id("kmp-library-test")
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.sqldelight)
//    id("koin-annotations")
}

kotlin {
    android {
        namespace = "org.example.calculator.data"
    }

    jvm()
    js {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.domain)

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)

            implementation(libs.sqlDelight.coroutines.extensions)
            implementation(libs.sqlDelight.primitive.adapters)

            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.coroutines)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)

            implementation(libs.sqlDelight.android.driver)
        }
        nativeMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.sqlDelight.native.driver)
        }
        jvmMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.sqlDelight.sqlite.driver)
        }
        jsMain.dependencies {
            implementation(kotlin("dom-api-compat"))
        }
        wasmJsMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-browser:0.5.0")
        }
    }
}

sqldelight {
    databases {
        create("SQLDelightDatabase") {
            packageName.set("com.example.calculator")
            generateAsync.set(true)
        }
    }
}

// koinCompiler {
//    compileSafety = false
// }
