package org.example.calculator_hard.domain

data class Calculation(
    val numbers: List<Float>,
    val operations: List<Operation>,
    val result: Double?
)
