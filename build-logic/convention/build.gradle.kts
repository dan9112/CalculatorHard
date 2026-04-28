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

gradlePlugin {
    plugins {
        register("kmpJvmBase") {
            // ID плагина, который вы будете вызывать в модулях
            id = "myproject.kmp.jvm.base"
            implementationClass = "KmpJvmBasePlugin"
        }
        register("kmpLibraryAndroid") {
            // ID плагина, который вы будете вызывать в модулях
            id = "myproject.kmp.library.android"
            implementationClass = "KmpLibraryAndroidPlugin"
        }
        register("kmpLibraryCompose") {
            // ID плагина, который вы будете вызывать в модулях
            id = "myproject.kmp.library.compose"
            implementationClass = "KmpLibraryComposePlugin"
        }
        register("kmpLibraryTest") {
            // ID плагина, который вы будете вызывать в модулях
            id = "myproject.kmp.library.test"
            implementationClass = "KmpLibraryTestPlugin"
        }
        register("koinAnnotations") {
            // ID плагина, который вы будете вызывать в модулях
            id = "myproject.koin.annotations"
            implementationClass = "KoinAnnotationsPlugin"
        }
    }
}
