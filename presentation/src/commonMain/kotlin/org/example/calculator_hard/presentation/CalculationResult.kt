package org.example.calculator_hard.presentation

sealed interface CalculationResult {
    data class Result(val number: Float = 0f) : CalculationResult
    data object DivideByZero : CalculationResult
}
