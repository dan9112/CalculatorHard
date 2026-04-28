plugins {
    kotlin("multiplatform")
    id("io.insert-koin.compiler.plugin")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(findCatalogLibrary("koin-bom")))
            implementation(findCatalogLibrary("koin-core"))
            api(findCatalogLibrary("koin-annotations"))
        }
        commonTest.dependencies {
            implementation(findCatalogLibrary("koin-test"))
        }
    }
}
