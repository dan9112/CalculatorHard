package org.example.calculator.presentation

import androidx.compose.runtime.snapshotFlow
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.calculator.domain.CalculationRepository
import org.example.calculator.domain.Operation
import org.example.calculator.domain.PageData
import org.example.calculator.domain.Calculation as DomainCalculation

interface RootComponent {
    val calculations: StateFlow<List<DomainCalculation>>
    val calculation: StateFlow<Calculation>
    val currentInput: StateFlow<String>
    val lastSavedId: StateFlow<Long?>
    val hasNext: StateFlow<Boolean>
    val hasPrevious: StateFlow<Boolean>

    fun appendDigit(digit: String)

    fun applyOperation(operation: Operation)

    fun calculateResult()

    fun backspace()

    fun clearCurrent()

    fun loadNext()

    fun loadPrevious()
}

private class RootComponentImpl(
    componentContext: ComponentContext,
    private val calculationRepository: CalculationRepository,
) : RootComponent,
    ComponentContext by componentContext {
    private val componentScope = componentContext.coroutineScope()
    private val pageSize = 12
    private val windowSize = 3

    private val currentPage = MutableStateFlow(0)
    private val _lastSavedId = MutableStateFlow<Long?>(null)
    private val _calculation = MutableStateFlow(Calculation())
    private val _currentInput = MutableStateFlow("")

    override val lastSavedId = _lastSavedId.asStateFlow()
    override val calculation = _calculation.asStateFlow()
    override val currentInput = _currentInput.asStateFlow()

    private val pageStates = mutableMapOf<Int, MutableStateFlow<PageData<DomainCalculation>>>()
    private val pageJobs = mutableMapOf<Int, Job>()

    @OptIn(ExperimentalCoroutinesApi::class)
    override val calculations: StateFlow<List<DomainCalculation>> =
        currentPage
            .flatMapLatest { startPage ->
                val indices = List(size = 3) { it + startPage }
                flow {
                    indices.forEach { idx ->
                        if (idx !in pageJobs) {
                            val state =
                                MutableStateFlow(
                                    PageData<DomainCalculation>(
                                        emptyList(),
                                        hasNext = false,
                                    ),
                                )
                            pageStates[idx] = state
                            pageJobs[idx] =
                                componentScope.launch {
                                    calculationRepository
                                        .getPageFlow(idx, pageSize)
                                        .collect { pageStates[idx]?.value = it }
                                }
                        }
                    }

                    val activeFlows = indices.mapNotNull { pageStates[it] }
                    if (activeFlows.isNotEmpty()) {
                        combine(activeFlows) { pages ->
                            pages.flatMap { it.items }
                        }.collect { emit(it) }
                    } else {
                        emit(emptyList())
                    }
                }
            }.stateIn(componentScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val hasPrevious =
        snapshotFlow { currentPage.value }
            .flatMapLatest { page ->
                val lastPageIdx = page + windowSize - 1
                pageStates[lastPageIdx]?.map { it.hasNext } ?: flowOf(false)
            }.stateIn(componentScope, SharingStarted.Eagerly, false)

    override val hasNext =
        currentPage.map { it > 0 }.stateIn(componentScope, SharingStarted.Eagerly, false)

    // Очистка старых страниц при сдвиге окна
    init {
        componentScope.launch {
            currentPage.collect { page ->
                val start = page
                val end = page + windowSize - 1
                val needed = (start..end).toSet()
                val toRemove = pageJobs.keys - needed
                toRemove.forEach { idx ->
                    pageJobs[idx]?.cancel()
                    pageJobs.remove(idx)
                    pageStates.remove(idx)
                }
            }
        }
    }

    override fun appendDigit(digit: String) {
        if (lastSavedId.value != null) clearCurrent()
        _currentInput.update { current ->
            if (digit == "." && current.contains(".")) return@update current
            if (current.length >= 12) return@update current
            when {
                current == "0" && digit != "." -> digit
                (current == "0" || current.isEmpty()) && digit == "." -> "0."
                else -> current + digit
            }
        }
    }

    override fun applyOperation(operation: Operation) {
        if (lastSavedId.value != null) clearCurrent()
        _calculation.update { currentCalc ->
            val inputNum = currentInput.value.toFloatOrNull() ?: 0f
            val hasInput = currentInput.value.isNotEmpty()
            if (currentCalc.operations.size == currentCalc.numbers.size && !hasInput && currentCalc.operations.isNotEmpty()) {
                if (currentCalc.operations.last() != operation) {
                    currentCalc.copy(operations = currentCalc.operations.dropLast(1) + operation)
                } else {
                    currentCalc
                }
            } else {
                currentCalc.copy(
                    numbers = currentCalc.numbers + inputNum,
                    operations = currentCalc.operations + operation,
                )
            }
        }
        _currentInput.value = ""
    }

    override fun calculateResult() {
        if (lastSavedId.value != null) return
        val inputNum = _currentInput.value.toFloatOrNull() ?: 0f
        val hasInput = _currentInput.value.isNotEmpty()
        _currentInput.value = ""
        val finalNumbers =
            if (hasInput) calculation.value.numbers + inputNum else calculation.value.numbers
        val result = calculate(finalNumbers, calculation.value.operations)
        _calculation.value =
            calculation.value.copy(
                numbers = finalNumbers,
                operations = calculation.value.operations,
                result = result,
            )
        if (result is CalculationResult.Result) {
            componentScope.launch {
                _lastSavedId.value =
                    calculationRepository.addCalculation(
                        finalNumbers,
                        calculation.value.operations,
                        result.number,
                    )
            }
        }
    }

    override fun backspace() {
        if (lastSavedId.value != null) {
            clearCurrent()
            return
        }

        val cleanInput: (Float) -> String = { num ->
            num.toString().removeSuffix(".0")
        }

        when {
            _currentInput.value.isNotEmpty() -> {
                _currentInput.value = _currentInput.value.dropLast(1)
            }

            _calculation.value.numbers.size > _calculation.value.operations.size -> {
                _calculation.update { calc ->
                    _currentInput.value = calc.numbers.lastOrNull()?.let(cleanInput) ?: ""
                    calc.copy(numbers = calc.numbers.dropLast(1))
                }
            }

            _calculation.value.numbers.isNotEmpty() -> {
                _calculation.update { calc ->
                    _currentInput.value = calc.numbers.lastOrNull()?.let(cleanInput) ?: ""
                    calc.copy(
                        numbers = calc.numbers.dropLast(1),
                        operations = calc.operations.dropLast(1),
                    )
                }
            }
        }
    }

    override fun clearCurrent() {
        _lastSavedId.value = null
        _calculation.value = Calculation()
        _currentInput.value = ""
    }

    override fun loadNext() {
        if (hasNext.value) currentPage.value--
    }

    override fun loadPrevious() {
        if (hasPrevious.value) currentPage.value++
    }

    private fun calculate(
        numbers: List<Float>,
        operations: List<Operation>,
    ): CalculationResult {
        if (numbers.isEmpty()) return CalculationResult.Result()
        val nums = numbers.toMutableList()
        val ops = operations.toMutableList()
        var i = 0
        while (i < ops.size) {
            when (ops[i]) {
                Operation.Mult -> {
                    nums[i] *= nums[i + 1]
                    nums.removeAt(i + 1)
                    ops.removeAt(i)
                }

                Operation.Div -> {
                    if (nums[i + 1] == 0f) return CalculationResult.DivideByZero
                    nums[i] /= nums[i + 1]
                    nums.removeAt(i + 1)
                    ops.removeAt(i)
                }

                else -> {
                    i++
                }
            }
        }
        return CalculationResult.Result(
            ops.zip(nums.drop(1)).fold(nums.first().toDouble()) { acc, (op, num) ->
                if (op == Operation.Plus) acc + num else acc - num
            },
        )
    }
}

fun createRootComponent(
    componentContext: ComponentContext,
    calculationRepository: CalculationRepository,
): RootComponent = RootComponentImpl(componentContext, calculationRepository)
