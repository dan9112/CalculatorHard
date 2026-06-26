plugins {
    kotlin("multiplatform")
}

kotlin {
    jvmToolchain(20)
    compilerOptions {
//        languageVersion.set(KotlinVersion.KOTLIN_2_3)
        freeCompilerArgs.set(listOf("-Xexplicit-backing-fields"))
    }
}
