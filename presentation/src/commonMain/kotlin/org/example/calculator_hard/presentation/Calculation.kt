package org.example.calculator_hard.presentation

import org.example.calculator_hard.domain.Operation

data class Calculation(
    val numbers: List<Float> = emptyList(),
    val operations: List<Operation> = emptyList(),
    val result: CalculationResult = CalculationResult.Result()
)
