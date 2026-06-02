package org.example.calculator_hard.data

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
