package org.example.calculator_hard.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.example.calculator_hard.domain.Calculation
import org.example.calculator_hard.domain.CalculationRepository
import org.example.calculator_hard.domain.Operation
import org.example.calculator_hard.domain.PageData

// todo: replace fake when SQLDelight dev team shows right way of using their library on web!
class FakeCalculationRepository : CalculationRepository {
    private var index = 0L
    private val _calculations = MutableStateFlow<List<Calculation>>(emptyList())

    override fun getPageFlow(page: Int, pageSize: Int) = _calculations.map { list ->
        val items = list
            .dropLast(page * pageSize)
            .takeLast(pageSize + 1)
            .reversed()

        val hasNext = items.size > pageSize
        PageData(
            items = if (hasNext) items.dropLast(1) else items,
            hasNext = hasNext
        )
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
