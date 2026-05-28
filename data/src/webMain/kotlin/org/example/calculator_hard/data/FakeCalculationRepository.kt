package org.example.calculator_hard.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.calculator_hard.domain.Calculation
import org.example.calculator_hard.domain.CalculationRepository

// todo: replace fake when SQLDelight dev team shows right way of using their library on web!
class FakeCalculationRepository : CalculationRepository {
    private val _calculations = MutableStateFlow<List<Calculation>>(value = emptyList())
    override val calculations = _calculations.asStateFlow()

    override suspend fun addCalculation(calculation: Calculation) {
        _calculations.update {
            it + calculation
        }
    }
}
