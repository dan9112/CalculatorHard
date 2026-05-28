package org.example.calculator_hard.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import org.example.calculator_hard.domain.Operation
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

// ✅ ИСХОДНЫЕ КОНСТАНТЫ ВОССТАНОВЛЕНЫ С ИСХОДНОЙ ВИДИМОСТЬЮ
private const val MIN_DISPLAY_WIDTH = 180
private const val MIN_DISPLAY_HEIGHT = 150
private const val MIN_BUTTON_SIZE = 45
private const val DISPLAY_HORIZONTAL_WEIGHT = 4
private const val DISPLAY_VERTICAL_WEIGHT = 3
private const val SPACE_BETWEEN_BUTTONS = 2
private const val BUTTONS_COLUMNS = 4
private const val BUTTONS_ROWS = 5

val MIN_BUTTONS_WIDTH =
    MIN_BUTTON_SIZE * BUTTONS_COLUMNS + SPACE_BETWEEN_BUTTONS * BUTTONS_COLUMNS.dec()
val MIN_BUTTONS_HEIGHT = MIN_BUTTON_SIZE * BUTTONS_ROWS + SPACE_BETWEEN_BUTTONS * BUTTONS_ROWS.dec()
private const val BUTTONS_HORIZONTAL_WEIGHT = 5
private const val BUTTONS_VERTICAL_WEIGHT = 4
private const val BUTTON_CORNERS = 10
const val PADDINGS = 5
val MIN_SCREEN_HORIZONTAL_WIDTH = MIN_DISPLAY_WIDTH + MIN_BUTTONS_WIDTH + PADDINGS * 3
val MIN_SCREEN_HORIZONTAL_HEIGHT = maxOf(MIN_DISPLAY_WIDTH, MIN_BUTTONS_WIDTH) + PADDINGS * 3
val MIN_SCREEN_VERTICAL_WIDTH = maxOf(MIN_DISPLAY_HEIGHT, MIN_BUTTONS_HEIGHT) + PADDINGS * 3
val MIN_SCREEN_VERTICAL_HEIGHT = MIN_DISPLAY_HEIGHT + MIN_BUTTONS_HEIGHT + PADDINGS * 3

@Composable
expect fun rememberOrientation(): Orientation

fun Double.formatDisplay() = formatWithPrecision(this, factor = 1_000_000L, precision = 6)

fun Float.formatDisplay() = when {
    isNaN() -> "NaN"
    this == Float.POSITIVE_INFINITY -> "∞"
    this == Float.NEGATIVE_INFINITY -> "-∞"
    else -> toString()
        .toDouble()
        .let { formatWithPrecision(it, factor = 100L, precision = 2) }
}

private fun formatWithPrecision(value: Double, factor: Long, precision: Int): String {
    if (value.isNaN()) return "NaN"
    if (value == Double.POSITIVE_INFINITY) return "∞"
    if (value == Double.NEGATIVE_INFINITY) return "-∞"

    // 1. Переводим в целые единицы в зависимости от фактора (100 или 1_000_000)
    val totalUnits = (value * factor).roundToLong().absoluteValue

    val intPart = totalUnits / factor
    val fracPart = totalUnits % factor

    // 2. Если после округления вышел чистый ноль, убираем минус
    if (intPart == 0L && fracPart == 0L) return "0"

    val sign = if (value < 0) "-" else ""
    if (fracPart == 0L) return "$sign$intPart"

    // 3. Заполняем нулями слева до нужной точности и удаляем незначащие нули справа
    val fracStr = fracPart
        .toString()
        .padStart(precision, '0')
        .trimEnd('0')

    return "$sign$intPart.$fracStr"
}

@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    rootComponent: RootComponent,
    orientation: Orientation = rememberOrientation()
) {
    Scaffold { innerPadding ->
        BoxWithConstraints(
            modifier = modifier
                .padding(innerPadding)
                .padding(all = PADDINGS.dp)
        ) {
            val width = maxWidth - PADDINGS.dp
            val height = maxHeight - PADDINGS.dp

            val targetDisplayWidth = when (orientation) {
                Orientation.Horizontal -> (width * DISPLAY_HORIZONTAL_WEIGHT / (DISPLAY_HORIZONTAL_WEIGHT + BUTTONS_HORIZONTAL_WEIGHT)).coerceAtMost(
                    width - MIN_BUTTONS_WIDTH.dp
                )

                Orientation.Vertical -> maxWidth
            }
            val targetDisplayHeight = when (orientation) {
                Orientation.Horizontal -> maxHeight
                Orientation.Vertical -> (height * DISPLAY_VERTICAL_WEIGHT / (DISPLAY_VERTICAL_WEIGHT + BUTTONS_VERTICAL_WEIGHT)).coerceAtMost(
                    height - MIN_BUTTONS_HEIGHT.dp
                )
            }
            val targetButtonsWidth = when (orientation) {
                Orientation.Horizontal -> width - targetDisplayWidth
                Orientation.Vertical -> maxWidth
            }
            val targetButtonsHeight = when (orientation) {
                Orientation.Horizontal -> maxHeight
                Orientation.Vertical -> height - targetDisplayHeight
            }

            val displayWidth by animateDpAsState(targetValue = targetDisplayWidth)
            val displayHeight by animateDpAsState(targetValue = targetDisplayHeight)
            val buttonsWidth by animateDpAsState(targetValue = targetButtonsWidth)
            val buttonsHeight by animateDpAsState(targetValue = targetButtonsHeight)

            val calculation by rootComponent.calculation.collectAsState()
            val currentInput by rootComponent.currentInput.collectAsState()
            val lastSavedId by rootComponent.lastSavedId.collectAsState()
            val visibleHistory by rootComponent.calculations.collectAsState() // Уже отфильтрован через combine

            val isSticky = lastSavedId != null

            // ✅ Формирование строки выражения
            val expression = buildString {
                calculation.operations.forEachIndexed { i, op ->
                    append("${calculation.numbers[i].formatDisplay()} ")
                    append(
                        when (op) {
                            Operation.Plus -> '+'; Operation.Minus -> '-'; Operation.Mult -> '*'; Operation.Div -> '/'
                        }
                    )
                    append(" ")
                }
                if (isSticky) {
                    val lastNum = if (calculation.numbers.size > calculation.operations.size)
                        calculation.numbers.last() else 0f
                    append("${lastNum.formatDisplay()} = ${(calculation.result as? CalculationResult.Result)?.number?.formatDisplay() ?: "Calculate error"}")
                } else {
                    append(
                        currentInput.ifEmpty {
                            if (calculation.numbers.size > calculation.operations.size)
                                calculation.numbers.last().formatDisplay() else "0"
                        }
                    )
                }
            }

            // ✅ Превью результата
            val previewResult = if (isSticky) {
                (calculation.result as? CalculationResult.Result)?.number?.formatDisplay()
                    ?: "Error"
            } else {
                calculatePreview(calculation.numbers, calculation.operations, currentInput)
            }

            Column(
                modifier = Modifier
                    .width(displayWidth).height(displayHeight)
                    .align(Alignment.TopStart)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(BUTTON_CORNERS.dp)
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                val listState = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(2.dp, alignment = Alignment.Bottom),
                    horizontalAlignment = Alignment.End
                ) {
                    visibleHistory?.let { storedResults ->
                        items(items = storedResults, key = { it.id }) { calc ->
                            Text(
                                text = buildString {
                                    calc.operations.forEachIndexed { index, op ->
                                        append("${calc.numbers[index].formatDisplay()} ")
                                        append(
                                            when (op) {
                                                Operation.Plus -> "+"; Operation.Minus -> "-"; Operation.Mult -> "*"; Operation.Div -> "/"
                                            }
                                        )
                                        append("  ")
                                    }
                                    append(
                                        "${
                                            calc.numbers.last().formatDisplay()
                                        } = ${calc.result?.formatDisplay() ?: "Error"}"
                                    )
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.6f
                                    )
                                )
                            )
                        }
                    } ?: item {
                        Text(
                            "Loading...",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                Text(
                    text = buildAnnotatedString {
                        append(expression)
                        appendLine()
                        withStyle(SpanStyle(fontWeight = if (isSticky) FontWeight.Bold else null)) {
                            append(previewResult)
                        }
                    },
                    modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.End
                )
            }

            StaticCalculatorGrid(
                modifier = Modifier.width(buttonsWidth).height(buttonsHeight)
                    .align(Alignment.BottomEnd),
                onDigit = rootComponent::appendDigit,
                onOperation = rootComponent::applyOperation,
                onEquals = rootComponent::calculateResult,
                onBackspace = rootComponent::backspace
            )
        }
    }
}

private fun calculatePreview(
    numbers: List<Float>,
    operations: List<Operation>,
    currentInput: String
): String {
    if (currentInput.isEmpty() && numbers.isEmpty()) return "0"
    val currentNum = currentInput.toFloatOrNull() ?: 0f
    val allNumbers = if (currentInput.isNotEmpty()) {
        if (numbers.size > operations.size) numbers.dropLast(1) + currentNum else numbers + currentNum
    } else numbers

    if (allNumbers.isEmpty()) return "0"
    if (allNumbers.size == 1 && operations.isEmpty()) return allNumbers[0].formatDisplay()

    val nums = allNumbers.toMutableList()
    val ops = operations.toMutableList()
    var idx = 0
    while (idx < ops.size) {
        when (ops[idx]) {
            Operation.Mult -> {
                nums[idx] *= nums[idx + 1]; nums.removeAt(idx + 1); ops.removeAt(idx)
            }

            Operation.Div -> {
                if (nums[idx + 1] == 0f) return "∞"; nums[idx] /= nums[idx + 1]; nums.removeAt(idx + 1); ops.removeAt(
                    idx
                )
            }

            else -> idx++
        }
    }
    val res = ops.zip(nums.drop(1))
        .fold(nums.first()) { acc, (op, n) -> if (op == Operation.Plus) acc + n else acc - n }
    return res.formatDisplay()
}

@Composable
private fun StaticCalculatorGrid(
    modifier: Modifier = Modifier,
    onDigit: (String) -> Unit,
    onOperation: (Operation) -> Unit,
    onEquals: () -> Unit,
    onBackspace: () -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val cellWidth =
            (maxWidth - SPACE_BETWEEN_BUTTONS.dp * (BUTTONS_COLUMNS - 1)) / BUTTONS_COLUMNS
        val cellHeight = (maxHeight - SPACE_BETWEEN_BUTTONS.dp * (BUTTONS_ROWS - 1)) / BUTTONS_ROWS

        @Composable
        fun GridButton(
            row: Int,
            col: Int,
            rowSpan: Int = 1,
            colSpan: Int = 1,
            text: String,
            onClick: () -> Unit
        ) {
            val width = (cellWidth * colSpan) + (SPACE_BETWEEN_BUTTONS.dp * (colSpan - 1))
            val height = (cellHeight * rowSpan) + (SPACE_BETWEEN_BUTTONS.dp * (rowSpan - 1))
            Button(
                onClick = onClick,
                modifier = Modifier.offset(
                    x = (cellWidth + SPACE_BETWEEN_BUTTONS.dp) * col,
                    y = (cellHeight + SPACE_BETWEEN_BUTTONS.dp) * row
                ).size(width, height),
                shape = RoundedCornerShape(BUTTON_CORNERS.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
            ) { Text(text) }
        }

        GridButton(0, 0, text = "*") { onOperation(Operation.Mult) }
        GridButton(0, 1, text = "/") { onOperation(Operation.Div) }
        GridButton(0, 2, text = "-") { onOperation(Operation.Minus) }
        GridButton(0, 3, text = "←") { onBackspace() }
        GridButton(1, 0, text = "7") { onDigit("7") }
        GridButton(1, 1, text = "8") { onDigit("8") }
        GridButton(1, 2, text = "9") { onDigit("9") }
        GridButton(1, 3, rowSpan = 2, text = "+") { onOperation(Operation.Plus) }
        GridButton(2, 0, text = "4") { onDigit("4") }
        GridButton(2, 1, text = "5") { onDigit("5") }
        GridButton(2, 2, text = "6") { onDigit("6") }
        GridButton(3, 0, text = "1") { onDigit("1") }
        GridButton(3, 1, text = "2") { onDigit("2") }
        GridButton(3, 2, text = "3") { onDigit("3") }
        GridButton(3, 3, rowSpan = 2, text = "=") { onEquals() }
        GridButton(4, 0, colSpan = 2, text = "0") { onDigit("0") }
        GridButton(4, 2, text = ".") { onDigit(".") }
    }
}

@Composable
@Preview
private fun RootScreenPreview() {
    RootScreen(
        rootComponent = object : RootComponent {
            override val calculations = MutableStateFlow(value = null)
            override val calculation = MutableStateFlow(value = Calculation())
            override val currentInput = MutableStateFlow(value = "0.0")
            override val lastSavedId = MutableStateFlow(value = null)

            override fun appendDigit(digit: String) {}
            override fun applyOperation(operation: Operation) {}
            override fun calculateResult() {}
            override fun backspace() {}
        }
    )
}
