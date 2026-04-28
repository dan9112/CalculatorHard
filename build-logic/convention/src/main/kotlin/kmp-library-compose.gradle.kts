plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(findCatalogLibrary("compose-runtime"))
            implementation(findCatalogLibrary("compose-foundation"))
            implementation(findCatalogLibrary("compose-material3"))
            implementation(findCatalogLibrary("compose-ui"))
            implementation(findCatalogLibrary("compose-components-resources"))
            implementation(findCatalogLibrary("compose-uiToolingPreview"))
            implementation(findCatalogLibrary("androidx-lifecycle-viewmodelCompose"))
            implementation(findCatalogLibrary("androidx-lifecycle-runtimeCompose"))
        }
    }
}
