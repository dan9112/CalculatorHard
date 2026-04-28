plugins {
    kotlin("multiplatform")
}

kotlin {
    sourceSets {
        commonTest.dependencies {
            implementation(findCatalogLibrary("kotlin-test"))
        }
    }
}
