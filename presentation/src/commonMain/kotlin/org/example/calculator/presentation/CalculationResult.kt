package org.example.calculator.presentation

sealed interface CalculationResult {
    data class Result(
        val number: Double = 0.0,
    ) : CalculationResult

    data object DivideByZero : CalculationResult
}
