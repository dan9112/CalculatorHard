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
        namespace = "org.example.calculator_hard.data"
    }

    iosArm64()
    iosSimulatorArm64()
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

            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)

            implementation(libs.sqlDelight.coroutines.extensions)
            implementation(libs.sqlDelight.primitive.adapters)
        }
        androidMain.dependencies {
            implementation(libs.sqlDelight.android.driver)
        }
        nativeMain.dependencies {
            implementation(libs.sqlDelight.native.driver)
        }
        jvmMain.dependencies {
            implementation(libs.sqlDelight.sqlite.driver)
        }
        jsMain.dependencies {
            implementation(libs.sqlDelight.web.driver)

            implementation(npm("sql.js", "1.14.1"))
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.3.2"))
        }

        wasmJsMain.dependencies {
            implementation(libs.kotlinx.browser)

            implementation(libs.sqlDelight.webWasm.driver)
            implementation(devNpm("copy-webpack-plugin", "14.0.0"))

            implementation(npm("sql.js", "1.14.1"))
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.3.2"))
        }
    }
}

sqldelight {
    databases {
        create("SQLDelightDatabase") {
            packageName.set("com.example.calculator_hard")
            generateAsync.set(true)
        }
    }
}

//koinCompiler {
//    compileSafety = false
//}
