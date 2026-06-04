plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidKotlinMultiplatformLibrary) apply false
    alias(libs.plugins.composeHotReload) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false

    id("com.diffplug.spotless") version "8.6.0"

//    alias(libs.plugins.kotzilla) apply false
}

spotless {
    val ktlintVersion = "1.8.0"
    // Настройки для Kotlin файлов
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**/*.kt")
        ktlint(ktlintVersion)
            .customRuleSets(
                listOf(
                    // Подключаем официальный набор правил для Jetpack Compose
                    "io.nlopez.compose.rules:ktlint:0.5.9",
                ),
            ).editorConfigOverride(
                mapOf(
                    // ГЛАВНОЕ ИСПРАВЛЕНИЕ:
                    // Запрещаем базовому Ktlint проверять имена функций, помеченных @Composable.
                    // Теперь за это отвечают правила Compose (они требуют PascalCase для UI).
                    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
                    // Рекомендация для Compose: разрешить запятые в конце списков аргументов (trailing comma)
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
        // Можно добавить форматтер для markdown, например, flexmark
    }
}
