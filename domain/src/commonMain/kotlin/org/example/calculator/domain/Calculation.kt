package org.example.calculator.domain

data class Calculation(
    val id: Long,
    val numbers: List<Float>,
    val operations: List<Operation>,
    val result: Double?,
)
