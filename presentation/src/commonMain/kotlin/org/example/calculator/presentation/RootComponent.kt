package org.example.calculator.presentation

import androidx.compose.runtime.snapshotFlow
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.coroutines.coroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
import org.example.calculator.domain.SettingsRepository
import org.example.calculator.presentation.RootComponent.Companion.step
import org.example.calculator.presentation.ui.theme.ContrastLevel
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import org.example.calculator.domain.Calculation as DomainCalculation

interface CalculationComponent {
    val calculations: StateFlow<List<DomainCalculation>>
    val calculation: StateFlow<Calculation>
    val currentInput: StateFlow<String>
    val lastSavedId: StateFlow<Long?>
    val hasNext: StateFlow<Boolean>
    val hasPrevious: StateFlow<Boolean>

    fun appendNumberChar(digit: Char)
    fun applyOperation(operation: Operation)
    fun calculateResult()
    fun backspace()
    fun clearCurrent()
    fun loadNext()
    fun loadPrevious()
}

interface SettingsComponent {
    val theme: StateFlow<ThemeAttributeValue<Boolean?>>
    val contrastLevel: StateFlow<ThemeAttributeValue<ContrastLevel>>
    val dynamic: StateFlow<ThemeAttributeValue<Boolean>>?

    fun updateTheme(newValue: Boolean?)
    fun updateContrastLevel(newValue: ContrastLevel)
    fun updateDynamic(newValue: Boolean)
}

interface RootComponent {
    val splashScreenFinished: StateFlow<Float>

    val calculationComponent: CalculationComponent
    val settingsComponent: SettingsComponent

    companion object {
        val step = 150.milliseconds
    }
}

private class RootComponentImpl(
    componentContext: ComponentContext,
    private val calculationRepository: CalculationRepository,
    private val settingsRepository: SettingsRepository,
    private val componentScope: CoroutineScope = componentContext.coroutineScope(),
) : RootComponent,
    ComponentContext by componentContext,
    CalculationComponent,
    SettingsComponent {
    // todo: replace with separate implementations!
    override val calculationComponent = this
    override val settingsComponent = this

    override val splashScreenFinished: StateFlow<Float>
        field = MutableStateFlow(value = 0f)

    init {
        componentScope.launch {
            val totalTime = 3.2.seconds
            var time = Duration.ZERO
            while (time < totalTime) {
                delay(step)
                time += step
                splashScreenFinished.value = (time / totalTime).toFloat()
            }
        }
    }

    private val pageSize = 12
    private val windowSize = 3

    private val currentPage = MutableStateFlow(0)
    override val lastSavedId: StateFlow<Long?>
        field = MutableStateFlow<Long?>(value = null)
    override val calculation: StateFlow<Calculation>
        field = MutableStateFlow(value = Calculation())
    override val currentInput: StateFlow<String>
        field = MutableStateFlow(value = "")

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
            }
            .stateIn(componentScope, SharingStarted.Eagerly, emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    override val hasPrevious =
        snapshotFlow { currentPage.value }
            .flatMapLatest { page ->
                val lastPageIdx = page + windowSize - 1
                pageStates[lastPageIdx]?.map { it.hasNext } ?: flowOf(false)
            }
            .stateIn(componentScope, SharingStarted.Eagerly, false)

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

    override fun appendNumberChar(digit: Char) {
        if (lastSavedId.value != null) clearCurrent()
        currentInput.update { current ->
            if (digit == '.' && current.contains(".")) return@update current
            if (current.length >= 12) return@update current
            when {
                current == "0" && digit != '.' -> "$digit"
                (current == "0" || current.isEmpty()) && digit == '.' -> "0."
                else -> current + digit
            }
        }
    }

    override fun applyOperation(operation: Operation) {
        if (lastSavedId.value != null) clearCurrent()
        calculation.update { currentCalc ->
            val inputNum = currentInput
                .value
                .toFloatOrNull()
                ?: 0f
            val hasInput = currentInput
                .value
                .isNotEmpty()
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
        currentInput.value = ""
    }

    override fun calculateResult() {
        if (lastSavedId.value != null) return
        val inputNum = currentInput.value.toFloatOrNull() ?: 0f
        val hasInput = currentInput.value.isNotEmpty()
        currentInput.value = ""
        val finalNumbers =
            if (hasInput) calculation.value.numbers + inputNum else calculation.value.numbers
        val result = calculate(finalNumbers, calculation.value.operations)
        calculation.value = calculation
            .value
            .copy(
                numbers = finalNumbers,
                operations = calculation.value.operations,
                result = result,
            )
        if (result is CalculationResult.Result) {
            componentScope.launch {
                lastSavedId.value =
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
            currentInput.value.isNotEmpty() -> {
                currentInput.value = currentInput.value.dropLast(1)
            }

            calculation.value.numbers.size > calculation.value.operations.size -> {
                calculation.update { calc ->
                    currentInput.value = calc.numbers.lastOrNull()?.let(cleanInput) ?: ""
                    calc.copy(numbers = calc.numbers.dropLast(1))
                }
            }

            calculation.value.numbers.isNotEmpty() -> {
                calculation.update { calc ->
                    currentInput.value = calc
                        .numbers
                        .lastOrNull()
                        ?.let(cleanInput)
                        ?: ""
                    calc.copy(
                        numbers = calc.numbers.dropLast(1),
                        operations = calc.operations.dropLast(1),
                    )
                }
            }
        }
    }

    override fun clearCurrent() {
        lastSavedId.value = null
        calculation.value = Calculation()
        currentInput.value = ""
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

    override val dynamic = settingsRepository
        .themeDynamicColors
        ?.map(transform = ThemeAttributeValue<Boolean>::Value)
        ?.stateIn(
            scope = componentScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeAttributeValue.Idle,
        )

    override val theme = settingsRepository
        .darkTheme
        .map(transform = ThemeAttributeValue<Boolean?>::Value)
        .stateIn(
            scope = componentScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeAttributeValue.Idle,
        )

    override val contrastLevel = settingsRepository
        .themeContrastLevel
        .map {
            ThemeAttributeValue.Value(
                when (it) {
                    true -> ContrastLevel.High
                    false -> ContrastLevel.Medium
                    null -> ContrastLevel.Normal
                },
            )
        }
        .stateIn(
            scope = componentScope,
            started = SharingStarted.Eagerly,
            initialValue = ThemeAttributeValue.Idle,
        )

    override fun updateDynamic(newValue: Boolean) = settingsRepository.updateThemeDynamicColors(newValue)

    override fun updateTheme(newValue: Boolean?) = settingsRepository.updateDarkTheme(newValue)

    override fun updateContrastLevel(newValue: ContrastLevel) = settingsRepository.updateThemeContrastLevel(
        newValue = when (newValue) {
            ContrastLevel.Normal -> null
            ContrastLevel.Medium -> false
            ContrastLevel.High -> true
        },
    )
}

fun createRootComponent(
    componentContext: ComponentContext,
    calculationRepository: CalculationRepository,
    settingsRepository: SettingsRepository,
): RootComponent = RootComponentImpl(componentContext, calculationRepository, settingsRepository)

sealed interface ThemeAttributeValue<out T> {
    data object Idle : ThemeAttributeValue<Nothing>
    data class Value<T>(val value: T) : ThemeAttributeValue<T>
}
