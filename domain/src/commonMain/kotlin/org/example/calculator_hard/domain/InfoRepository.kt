package org.example.calculator_hard.domain

interface InfoRepository {
    suspend fun getInfo(): Info
}
