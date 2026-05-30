package org.example.calculator_hard.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.calculator_hard.domain.CalculationRepository
import org.example.calculator_hard.domain.Operation
import org.example.calculator_hard.domain.Calculation as DomainCalculation

interface RootComponent {
    val calculations: StateFlow<List<DomainCalculation>> // Отфильтрованный список
    val calculation: StateFlow<Calculation>
    val currentInput: StateFlow<String>
    val lastSavedId: StateFlow<Long?>

    val isLoadingMore: StateFlow<Boolean>
    val hasMore: StateFlow<Boolean>

    fun appendDigit(digit: String)
    fun applyOperation(operation: Operation)
    fun calculateResult()
    fun backspace()
    fun loadMore()
    fun deleteCalculation(id: Long)
}

private class RootComponentImpl(
    componentContext: ComponentContext,
    private val calculationRepository: CalculationRepository
) : RootComponent, ComponentContext by componentContext {
    private val componentScope = componentContext.coroutineScope()

    private val _lastSavedId = MutableStateFlow<Long?>(value = null)
    override val lastSavedId = _lastSavedId.asStateFlow()

    private val _allCalculations = MutableStateFlow<List<DomainCalculation>>(value = emptyList())
    private val _isLoadingMore = MutableStateFlow(value = false)
    private val _hasMore = MutableStateFlow(value = true)

    override val isLoadingMore = _isLoadingMore.asStateFlow()
    override val hasMore = _hasMore.asStateFlow()

    private var offset = 0
    private val limit = 30

    init {
        loadMore()
    }

    override val calculations = combine(
        _allCalculations,
        lastSavedId
    ) { list, savedId ->
        // Т.к. ORDER BY id DESC, последний сохранённый элемент всегда в начале (index 0)
        if (savedId != null && list.isNotEmpty() && list.first().id == savedId) {
            list.drop(1)
        } else list
    }.stateIn(scope = componentScope, started = SharingStarted.Eagerly, initialValue = emptyList())

    override fun loadMore() {
        if (_isLoadingMore.value || !_hasMore.value) return
        componentScope.launch {
            _isLoadingMore.value = true
            try {
                // Берём первую эмиссию потока для текущей страницы
                val page = calculationRepository.getCalculationsFlow(limit, offset).first()

                if (page.isNotEmpty()) {
                    _allCalculations.update { it + page }
                    offset += limit
                    // Если пришло меньше элементов, чем лимит → значит, страница последняя
                    if (page.size < limit) _hasMore.value = false
                } else {
                    _hasMore.value = false
                }
            } finally {
                _isLoadingMore.value = false
            }
        }
    }

    private val _calculation = MutableStateFlow(Calculation())
    override val calculation = _calculation.asStateFlow()

    private val _currentInput = MutableStateFlow("")
    override val currentInput = _currentInput.asStateFlow()

    // 🔹 Безопасный расчёт с приоритетами
    private fun calculate(numbers: List<Float>, operations: List<Operation>): CalculationResult {
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
                    // ✅ ТОЧНАЯ ПРОВЕРКА НА НОЛЬ (как запрошено)
                    if (nums[i + 1] == 0f) return CalculationResult.DivideByZero
                    nums[i] /= nums[i + 1]
                    nums.removeAt(i + 1)
                    ops.removeAt(i)
                }

                else -> i++
            }
        }

        val res = ops
            .zip(other = nums.drop(1))
            .fold(initial = nums.first().toDouble()) { acc, (op, num) ->
                if (op == Operation.Plus) acc + num else acc - num
            }
        return CalculationResult.Result(res)
    }

    override fun appendDigit(digit: String) {
        if (lastSavedId.value != null) {
            _lastSavedId.value = null
            _calculation.value = Calculation()
            _currentInput.value = ""
        }

        _currentInput.update { current ->
            if (digit == "." && current.contains(".")) return@update current
            if (current.length >= 12) return@update current
            when {
                current == "0" && digit != "." -> digit
                current == "0" && digit == "." -> "0."
                current.isEmpty() && digit == "." -> "0."
                else -> current + digit
            }
        }
    }

    override fun applyOperation(operation: Operation) {
        _calculation.update {
            if (lastSavedId.value != null) {
                _lastSavedId.value = null
            }

            if (it.operations.size == it.numbers.size && currentInput.value.isEmpty() && it.operations.isNotEmpty()) {
                if (it.operations.last() != operation) {
                    Calculation(operations = it.operations.dropLast(1) + operation)
                } else {
                    it
                }
            } else {
                Calculation(
                    numbers = it.numbers + (currentInput.value.toFloatOrNull() ?: 0f),
                    operations = it.operations + operation
                )
            }
        }
        _currentInput.value = ""
    }

    override fun calculateResult() {
        val inputNum = _currentInput.value.toFloatOrNull() ?: 0f
        val hasInput = _currentInput.value.isNotEmpty()
        _currentInput.value = ""

        val finalNumbers =
            if (hasInput) calculation.value.numbers + inputNum else calculation.value.numbers
        val result = calculate(finalNumbers, calculation.value.operations)
        _calculation.value = calculation.value.copy(
            numbers = finalNumbers,
            operations = calculation.value.operations,
            result = result
        )

        componentScope.launch {
            if (result is CalculationResult.Result) {
                val newId = calculationRepository.addCalculation(
                    numbers = finalNumbers,
                    operations = calculation.value.operations,
                    result = result.number
                )
                _lastSavedId.value = newId
            }
        }
    }

    override fun backspace() {
        when {
            lastSavedId.value != null -> {
                _lastSavedId.value = null
            }

            _currentInput.value.isNotEmpty() -> {
                _currentInput.value = _currentInput.value.dropLast(1)
            }

            _calculation.value.numbers.size > _calculation.value.operations.size -> {
                _calculation.update {
                    _currentInput.value = it.numbers.last().toString()
                    it.copy(numbers = it.numbers.dropLast(1))
                }
            }

            _calculation.value.numbers.isNotEmpty() -> {
                _calculation.update {
                    _currentInput.value = it.numbers.last().toString()
                    it.copy(
                        numbers = it.numbers.dropLast(1),
                        operations = it.operations.dropLast(1)
                    )
                }
            }
        }
    }

    override fun deleteCalculation(id: Long) {
        componentScope.launch {
            calculationRepository.deleteCalculationById(id)
            _allCalculations.update { calc -> calc.filter { it.id != id } }
        }
    }
}

fun createRootComponent(
    componentContext: ComponentContext,
    calculationRepository: CalculationRepository
): RootComponent =
    RootComponentImpl(componentContext, calculationRepository)
