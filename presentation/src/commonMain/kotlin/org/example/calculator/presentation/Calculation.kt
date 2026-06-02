package org.example.calculator.presentation

import org.example.calculator.domain.Operation

data class Calculation(
    val numbers: List<Float> = emptyList(),
    val operations: List<Operation> = emptyList(),
    val result: CalculationResult = CalculationResult.Result(),
)
