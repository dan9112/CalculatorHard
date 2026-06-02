package org.example.calculator.domain

interface InfoRepository {
    suspend fun getInfo(): Info
}
