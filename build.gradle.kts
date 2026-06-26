plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    alias(libs.plugins.spotless)

    alias(libs.plugins.kotzilla) apply true
}

spotless {
    val ktlintVersion = libs.versions.ktlint.get()
    // Настройки для Kotlin файлов
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint(ktlintVersion)
            .customRuleSets(
                listOf(
                    libs.ktlint.compose.rules
                        .get()
                        .toString(),
                ),
            ).editorConfigOverride(
                mapOf(
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                    "ktlint_standard_trailing-comma-on-declaration-site" to "enabled",
                    "ktlint_standard_trailing-comma-on-call-site" to "enabled",
                ),
            )
    }

    // Настройки для Kotlin Gradle Scripts (build.gradle.kts)
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude("**/build/**/*.gradle.kts")
        ktlint(ktlintVersion)
    }

    // Настройки для Markdown
    format("markdown") {
        target("**/*.md")
    }
}
