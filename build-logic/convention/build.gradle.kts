plugins {
    `kotlin-dsl`
}

dependencies {
    // Подключаем KGP, чтобы в .gradle.kts скриптах работал kotlin("multiplatform")
    implementation(libs.gradlePlugin.kotlin)
    implementation(libs.gradlePlugin.compose)
    implementation(libs.gradlePlugin.composeCompiler)
    implementation(libs.gradlePlugin.kotlinMultiplatformLibrary)
    implementation(libs.gradlePlugin.koinCompilerPlugin)
}
