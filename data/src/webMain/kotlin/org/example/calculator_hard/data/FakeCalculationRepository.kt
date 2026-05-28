package org.example.calculator_hard.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.calculator_hard.domain.Calculation
import org.example.calculator_hard.domain.CalculationRepository
import org.example.calculator_hard.domain.Operation

// todo: replace fake when SQLDelight dev team shows right way of using their library on web!
class FakeCalculationRepository : CalculationRepository {
    private var index = 0L
    private val _calculations = MutableStateFlow<List<Calculation>>(value = emptyList())
    override val calculations = _calculations.asStateFlow()

    override suspend fun addCalculation(
        numbers: List<Float>,
        operations: List<Operation>,
        result: Double?
    ): Long {
        val current = index++
        _calculations.update {
            it + Calculation(
                id = current,
                numbers = numbers,
                operations = operations,
                result = result
            )
        }
        return current
    }
}
