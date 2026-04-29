package org.example.calculator_hard.presentation

import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.absoluteValue

interface RootComponent {
    val calculation: StateFlow<Calculation>

    fun addOperation(operation: Operation)
    fun finishCalculation()
    fun addNumber(number: Float)
    fun removeLastSegment(): Float
}

private class RootComponentImpl(componentContext: ComponentContext) : RootComponent,
    ComponentContext by componentContext {
    private val _calculation = MutableStateFlow(value = Calculation())
    override val calculation = _calculation.asStateFlow()

    override fun addOperation(operation: Operation) {
        _calculation.update {
            when {
                it.stored -> {
                    Calculation(
                        numbers = listOf(0f),
                        operations = listOf(operation)
                    )
                }

                it.numbers.isEmpty() -> {
                    it.copy(
                        numbers = listOf(0f),
                        operations = listOf(operation),
                        result = CalculationResult.Result()
                    )
                }

                it.operations.size == it.numbers.size -> {
                    val newOperations = if (it.operations.last() != operation) {
                        it.operations.dropLast(1) + operation
                    } else {
                        it.operations
                    }
                    it.copy(
                        operations = newOperations,
                        result = calculate(numbers = it.numbers, operations = newOperations)
                    )
                }

                else -> {
                    val newOperations = it.operations + operation
                    it.copy(
                        operations = newOperations,
                        result = calculate(numbers = it.numbers, operations = newOperations)
                    )
                }
            }
        }
    }

    override fun finishCalculation() {
        // todo: add storing later!
        _calculation.update {
            if (it.stored) it else it.copy(stored = true)
        }
    }

    override fun removeLastSegment(): Float {
        var number = 0f
        _calculation.update {
            number = it.numbers.last()
            it.copy(
                numbers = it.numbers.dropLast(1),
                operations = it.operations.dropLast(1)
            )
        }
        return number
    }

    override fun addNumber(number: Float) {
        _calculation.update {
            if (it.stored) {
                Calculation(numbers = listOf(number), result = CalculationResult.Result(number))
            } else {
                when {
                    it.numbers.size == it.operations.size -> {
                        val newNumbers = it.numbers + number
                        it.copy(
                            numbers = newNumbers,
                            result = calculate(numbers = newNumbers, operations = it.operations)
                        )
                    }

                    it.numbers.last() == number -> {
                        it
                    }

                    else -> {
                        val newNumbers = it.numbers.dropLast(1) + number
                        it.copy(
                            numbers = newNumbers,
                            result = calculate(numbers = newNumbers, operations = it.operations)
                        )
                    }
                }
            }
        }
    }


    private fun calculate(numbers: List<Float>, operations: List<Operation>): CalculationResult {
        val numbers = numbers.toMutableList()
        val operations = operations.toMutableList()
        if (numbers.size == operations.size) numbers.add(0f)

        var index = 0
        while (index < operations.size) {
            when (operations[index]) {
                Operation.Mult -> {
                    numbers[index] *= numbers[index + 1]
                    operations.removeAt(index)
                    numbers.removeAt(index + 1)
                }

                Operation.Div -> {
                    if ((numbers[index + 1] - 0).absoluteValue < 1e-6f) return CalculationResult.DivideByZero
                    numbers[index] /= numbers[index + 1]
                    operations.removeAt(index)
                    numbers.removeAt(index + 1)
                }

                else -> {
                    index++
                }
            }
        }

        return CalculationResult.Result(
            number = operations
                .zip(other = numbers.drop(1))
                .fold(initial = numbers.first()) { previous, (operation, number) ->
                    if (operation == Operation.Plus) previous + number else previous - number
                }
        )
    }
}

fun createRootComponent(componentContext: ComponentContext): RootComponent =
    RootComponentImpl(componentContext)
