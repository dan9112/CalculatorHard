package org.example.calculator_hard.presentation

import org.example.calculator_hard.domain.InfoRepository
import org.koin.core.annotation.Factory

@Factory
class Greeting(private val infoRepository: InfoRepository) {
    suspend fun greet() = "Hello, ${infoRepository.getInfo().name}!"
}
