import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    id("kmp-jvm-base")
    id("kmp-library-compose")
    id("kmp-library-test")
    alias(libs.plugins.composeHotReload)
}

kotlin {
    jvm()

    js {
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(projects.shared)

//            implementation(project.dependencies.platform(libs.koin.bom))
//            implementation(libs.koin.core)
        }
        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutinesSwing)
        }
        webMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
    }
}

compose.desktop {
    application {
        mainClass = "org.example.calculator_hard.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.calculator_hard"
            packageVersion = "1.0.0"
        }
    }
}
