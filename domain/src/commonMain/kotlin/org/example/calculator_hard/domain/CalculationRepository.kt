package org.example.calculator_hard.domain

import kotlinx.coroutines.flow.Flow

interface CalculationRepository {
    val calculations: Flow<List<Calculation>>

    suspend fun addCalculation(
        numbers: List<Float>,
        operations: List<Operation>,
        result: Double?
    ): Long
}
