package org.example.calculator_hard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
