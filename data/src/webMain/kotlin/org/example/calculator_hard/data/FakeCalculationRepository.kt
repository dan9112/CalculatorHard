package org.example.calculator_hard.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.example.calculator_hard.domain.Calculation
import org.example.calculator_hard.domain.CalculationRepository
import org.example.calculator_hard.domain.Operation

// todo: replace fake when SQLDelight dev team shows right way of using their library on web!
class FakeCalculationRepository : CalculationRepository {
    private var index = 0L
    private val _calculations = MutableStateFlow<List<Calculation>>(emptyList())

    // Реактивная пагинация с сортировкой по убыванию ID
    override fun getCalculationsFlow(limit: Int, offset: Int): Flow<List<Calculation>> {
        return _calculations.map { list ->
            list.sortedByDescending { it.id }
                .drop(offset)
                .take(limit)
        }
    }

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

    override suspend fun deleteCalculationById(id: Long) {
        _calculations.update { list ->
            list.filter { it.id != id }
        }
    }
}
