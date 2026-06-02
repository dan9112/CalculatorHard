package org.example.calculator.data

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
