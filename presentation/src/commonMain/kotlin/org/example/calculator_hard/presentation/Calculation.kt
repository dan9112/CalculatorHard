package org.example.calculator_hard.presentation

data class Calculation(
    val numbers: List<Float> = emptyList(),
    val operations: List<Operation> = emptyList(),
    val result: CalculationResult = CalculationResult.Result(),
    val stored: Boolean = false
)
