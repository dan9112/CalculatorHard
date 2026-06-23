package org.example.calculator.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import calculator.presentation.generated.resources.Res
import calculator.presentation.generated.resources.copy
import calculator.presentation.generated.resources.divide_by_zero
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.example.calculator.domain.Operation
import org.example.calculator.presentation.CalculationResult.DivideByZero
import org.example.calculator.presentation.CalculationResult.Result
import org.example.calculator.presentation.settings.ExpandableSettingsPanel
import org.example.calculator.presentation.ui.theme.ContrastLevel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue
import kotlin.math.roundToLong
import org.example.calculator.domain.Calculation as DomainCalculation

private const val MIN_DISPLAY_WIDTH = 180
private const val MIN_DISPLAY_HEIGHT = 150
private const val MIN_BUTTON_SIZE = 45
private const val DISPLAY_HORIZONTAL_WEIGHT = 4
private const val DISPLAY_VERTICAL_WEIGHT = 3
private const val SPACE_BETWEEN_BUTTONS = 2
private const val BUTTONS_COLUMNS = 4
private const val BUTTONS_ROWS = 5
const val MIN_BUTTONS_WIDTH =
    MIN_BUTTON_SIZE * BUTTONS_COLUMNS + SPACE_BETWEEN_BUTTONS * (BUTTONS_COLUMNS - 1)
const val MIN_BUTTONS_HEIGHT =
    MIN_BUTTON_SIZE * BUTTONS_ROWS + SPACE_BETWEEN_BUTTONS * (BUTTONS_ROWS - 1)
private const val BUTTONS_HORIZONTAL_WEIGHT = 5
private const val BUTTONS_VERTICAL_WEIGHT = 4
private const val BUTTON_CORNERS = 10
const val PADDINGS = 5
const val MIN_SCREEN_HORIZONTAL_WIDTH = MIN_DISPLAY_WIDTH + MIN_BUTTONS_WIDTH + PADDINGS * 3
val MIN_SCREEN_HORIZONTAL_HEIGHT = maxOf(MIN_DISPLAY_WIDTH, MIN_BUTTONS_WIDTH) + PADDINGS * 3
val MIN_SCREEN_VERTICAL_WIDTH = maxOf(MIN_DISPLAY_HEIGHT, MIN_BUTTONS_HEIGHT) + PADDINGS * 3
const val MIN_SCREEN_VERTICAL_HEIGHT = MIN_DISPLAY_HEIGHT + MIN_BUTTONS_HEIGHT + PADDINGS * 3

private const val NOT_A_NUMBER = "NaN"
private const val INFINITY = "∞"

data class VerticalPadding(val top: Dp, val bottom: Dp)

@Composable
expect fun rememberOrientation(): Orientation

fun Double.formatDisplay() = formatWithPrecision(value = this, factor = 1_000_000L, precision = 6)
fun Float.formatDisplay() = when {
    isNaN() -> NOT_A_NUMBER
    this == Float.POSITIVE_INFINITY -> INFINITY
    this == Float.NEGATIVE_INFINITY -> "-$INFINITY"
    else -> formatWithPrecision(value = toString().toDouble(), factor = 100L, precision = 2)
}

private fun formatWithPrecision(value: Double, factor: Long, precision: Int): String {
    if (value.isNaN()) return NOT_A_NUMBER
    if (value == Double.POSITIVE_INFINITY) return INFINITY
    if (value == Double.NEGATIVE_INFINITY) return "-$INFINITY"
    val totalUnits = (value * factor).roundToLong().absoluteValue
    val intPart = totalUnits / factor
    val fracPart = totalUnits % factor
    if (intPart == 0L && fracPart == 0L) return "0"
    return buildString {
        if (value < 0) append('-')
        append(intPart)
        if (fracPart == 0L) return@buildString
        val fracStr = fracPart
            .toString()
            .padStart(precision, '0')
            .trimEnd('0')
        append(".$fracStr")
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RootScreen(
    component: CalculationComponent,
    modifier: Modifier = Modifier,
    padding: VerticalPadding = VerticalPadding(top = 0.dp, bottom = 0.dp),
    theme: Boolean? = null,
    updateTheme: (Boolean?) -> Unit = { },
    contrast: ContrastLevel = ContrastLevel.Normal,
    updateContrast: (ContrastLevel) -> Unit = { },
    dynamic: Boolean = false,
    updateDynamic: ((Boolean) -> Unit)? = null,
    orientation: Orientation = rememberOrientation(),
) {
    BoxWithConstraints(
        modifier = modifier.onPreviewKeyEvent { event ->
            if (event.type == KeyEventType.KeyDown) {
                when (event.key) {
                    Key.NumPad0, Key.Zero -> component.appendNumberChar(digit = '0')

                    Key.NumPad1, Key.One -> component.appendNumberChar(digit = '1')

                    Key.NumPad2, Key.Two -> component.appendNumberChar(digit = '2')

                    Key.NumPad3, Key.Three -> component.appendNumberChar(digit = '3')

                    Key.NumPad4, Key.Four -> component.appendNumberChar(digit = '4')

                    Key.NumPad5, Key.Five -> component.appendNumberChar(digit = '5')

                    Key.NumPad6, Key.Six -> component.appendNumberChar(digit = '6')

                    Key.NumPad7, Key.Seven -> component.appendNumberChar(digit = '7')

                    Key.NumPad8, Key.Eight -> component.appendNumberChar(digit = '8')

                    Key.NumPad9, Key.Nine -> component.appendNumberChar(digit = '9')

                    Key.Comma,
                    Key.Period,
                    Key.NumPadComma,
                    Key.NumPadDot,
                    -> component.appendNumberChar(digit = '.')

                    Key.Minus -> component.applyOperation(Operation.Minus)

                    Key.Plus, Key.NumPadAdd -> component.applyOperation(Operation.Plus)

                    Key.Multiply, Key.NumPadMultiply -> component.applyOperation(
                        Operation.Mult,
                    )

                    Key.Slash, Key.NumPadDivide -> component.applyOperation(
                        Operation.Div,
                    )

                    Key.Backspace -> component.backspace()

                    Key.Enter,
                    Key.Equals,
                    Key.NumPadEnter,
                    Key.NumPadEquals,
                    -> component.calculateResult()

                    Key.Escape -> component.clearCurrent()

                    else -> return@onPreviewKeyEvent false
                }
                true
            } else {
                false
            }
        },
    ) {
        val width = maxWidth - when (orientation) {
            Orientation.Vertical -> 0.dp
            Orientation.Horizontal -> PADDINGS.dp
        }
        val height =
            maxHeight - padding.top - padding.bottom - when (orientation) {
                Orientation.Vertical -> PADDINGS.dp
                Orientation.Horizontal -> 0.dp
            }

        val targetDisplayWidth = when (orientation) {
            Orientation.Horizontal -> (width * DISPLAY_HORIZONTAL_WEIGHT / (DISPLAY_HORIZONTAL_WEIGHT + BUTTONS_HORIZONTAL_WEIGHT))
                .coerceAtMost(maximumValue = width - MIN_BUTTONS_WIDTH.dp)

            Orientation.Vertical -> maxWidth
        }
        val targetDisplayHeight = when (orientation) {
            Orientation.Horizontal -> maxHeight - padding.bottom

            Orientation.Vertical -> (height * DISPLAY_VERTICAL_WEIGHT / (DISPLAY_VERTICAL_WEIGHT + BUTTONS_VERTICAL_WEIGHT))
                .coerceAtMost(maximumValue = height - MIN_BUTTONS_HEIGHT.dp) + padding.top
        }
        val targetButtonsWidth = when (orientation) {
            Orientation.Horizontal -> width - targetDisplayWidth
            Orientation.Vertical -> maxWidth
        }
        val targetButtonsHeight = when (orientation) {
            Orientation.Horizontal -> maxHeight - padding.top - padding.bottom
            Orientation.Vertical -> height - targetDisplayHeight + padding.top
        }

        val displayWidth by animateDpAsState(targetValue = targetDisplayWidth)
        val displayHeight by animateDpAsState(targetValue = targetDisplayHeight)
        val buttonsWidth by animateDpAsState(targetValue = targetButtonsWidth)
        val buttonsHeight by animateDpAsState(targetValue = targetButtonsHeight)

        val calculation by component.calculation.collectAsState()
        val currentInput by component.currentInput.collectAsState()
        val lastSavedId by component.lastSavedId.collectAsState()
        val visibleHistory by component.calculations.collectAsState()
        val hasOlder by component.hasPrevious.collectAsState()
        val hasNewer by component.hasNext.collectAsState()
        val listState = rememberLazyListState()

        LaunchedEffect(visibleHistory.firstOrNull()?.id) {
            if (visibleHistory.isNotEmpty()) listState.animateScrollToItem(0)
        }

        LaunchedEffect(listState) {
            snapshotFlow { listState.canScrollForward }
                .distinctUntilChanged()
                .collect { canScrollForward ->
                    if (!canScrollForward && hasOlder) {
                        component.loadPrevious()
                    }
                }
        }
        LaunchedEffect(listState) {
            snapshotFlow { listState.canScrollBackward }
                .distinctUntilChanged()
                .collect { canScrollBackward ->
                    if (!canScrollBackward && hasNewer) {
                        component.loadNext()
                    }
                }
        }

        val isSticky = lastSavedId != null

        val expression = buildString {
            calculation.run {
                operations.forEachIndexed { i, op ->
                    val number = numbers[i].formatDisplay()
                    if (i == 0 && number == "0" && numbers.size > 1 && op == Operation.Minus) {
                        append("- ")
                    } else {
                        append("$number ")
                        append(
                            when (op) {
                                Operation.Plus -> "+ "
                                Operation.Minus -> "- "
                                Operation.Mult -> "* "
                                Operation.Div -> "/ "
                            },
                        )
                    }
                }
                when {
                    isSticky -> {
                        val lastNum =
                            if (numbers.size > operations.size) numbers.last() else 0f
                        append("${lastNum.formatDisplay()} = ${(result as? Result)?.number?.formatDisplay() ?: "Calculate error"}")
                    }

                    numbers.size > operations.size -> {
                        append(
                            numbers
                                .last()
                                .formatDisplay(),
                        )
                    }

                    currentInput.isNotEmpty() -> {
                        append(currentInput)
                    }

                    numbers.isEmpty() -> {
                        append("0")
                    }
                }
            }
        }

        val previewResult by rememberUpdatedState(
            newValue = when (
                val current = calculation.run {
                    if (isSticky) {
                        result
                    } else {
                        calculatePreview(numbers, operations, currentInput)
                    }
                }
            ) {
                DivideByZero -> stringResource(resource = Res.string.divide_by_zero)

                is Result ->
                    current
                        .number
                        .formatDisplay()
            },
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopStart)
                .width(displayWidth)
                .height(displayHeight),
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(top = padding.top),
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(
                    2.dp,
                    alignment = Alignment.Bottom,
                ),
                horizontalAlignment = Alignment.End,
            ) {
                items(visibleHistory, key = { it.id }) { calc ->
                    Text(
                        text = buildString {
                            calc.operations.forEachIndexed { index, op ->
                                val number = calc.numbers[index].formatDisplay()
                                if (index == 0 && number == "0" && calc.numbers.size > 1 && calc.operations.first() == Operation.Minus) {
                                    append("- ")
                                } else {
                                    append("$number ")
                                    append(
                                        when (op) {
                                            Operation.Plus -> "+ "
                                            Operation.Minus -> "- "
                                            Operation.Mult -> "* "
                                            Operation.Div -> "/ "
                                        },
                                    )
                                }
                            }
                            calc.numbers.lastOrNull()?.let { lastNum ->
                                append("${lastNum.formatDisplay()} = ${calc.result?.formatDisplay() ?: "Error"}")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                                alpha = 0.6f,
                            ),
                        ),
                    )
                }
            }

            @Composable
            fun CurrentCalculationText(
                text: String,
                fontWeight: FontWeight? = null,
            ) = Text(
                text = text,
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = fontWeight,
                ),
            )

            CurrentCalculationText(text = expression)

            AppContextMenu(
                menuContent = {
                    item(label = Res.string.copy) { copyToClipboard(text = previewResult) }
                },
            ) {
                CurrentCalculationText(
                    text = previewResult,
                    fontWeight = if (isSticky) FontWeight.Bold else null,
                )
            }
        }

        StaticCalculatorGrid(
            modifier = Modifier
                .padding(bottom = padding.bottom)
                .width(buttonsWidth)
                .height(buttonsHeight)
                .align(Alignment.BottomEnd),
            onChar = component::appendNumberChar,
            onOperation = component::applyOperation,
            onEquals = component::calculateResult,
            onBackspace = component::backspace,
        )

        ExpandableSettingsPanel(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = padding.top),
            buttonShape = RoundedCornerShape(size = BUTTON_CORNERS.dp),
            theme = theme to updateTheme,
            contrast = contrast to updateContrast,
            dynamic = updateDynamic?.let { update -> dynamic to update },
        )
    }
}

private fun calculatePreview(
    numbers: List<Float>,
    operations: List<Operation>,
    currentInput: String,
): CalculationResult {
    if (currentInput.isEmpty() && numbers.isEmpty()) return Result(number = 0.0)
    val currentNum = currentInput
        .toFloatOrNull()
        ?: 0f
    val allNumbers = if (currentInput.isNotEmpty()) {
        if (numbers.size > operations.size) {
            numbers.dropLast(1) + currentNum
        } else {
            numbers + currentNum
        }
    } else {
        numbers
    }
    if (allNumbers.isEmpty()) return Result(number = 0.0)
    if (allNumbers.size == 1 && operations.isEmpty()) {
        return Result(
            number = allNumbers
                .first()
                .toDouble(),
        )
    }

    val numbs = allNumbers.toMutableList()
    val ops = operations.toMutableList()
    var idx = 0
    while (idx < ops.size) {
        when (ops[idx]) {
            Operation.Mult -> {
                if (idx < numbs.lastIndex) {
                    numbs[idx] *= numbs[idx + 1]
                    numbs.removeAt(idx + 1)
                    ops.removeAt(idx)
                } else {
                    idx++
                }
            }

            Operation.Div -> {
                if (idx < numbs.lastIndex) {
                    if (numbs[idx + 1] == 0f) return DivideByZero
                    numbs[idx] /= numbs[idx + 1]
                    numbs.removeAt(idx + 1)
                    ops.removeAt(idx)
                } else {
                    idx++
                }
            }

            else -> {
                idx++
            }
        }
    }

    return Result(
        number = ops
            .zip(other = numbs.drop(1))
            .fold(initial = numbs.first()) { acc, (op, n) -> if (op == Operation.Plus) acc + n else acc - n }
            .toDouble(),
    )
}

internal expect class ContextItemScope {
    fun item(label: String, onClick: () -> Unit)
    fun item(label: StringResource, onClick: () -> Unit)
}

internal expect fun ContextItemScope.copyToClipboard(text: String)

@Composable
internal expect fun AppContextMenu(
    menuContent: ContextItemScope.() -> Unit,
    content: @Composable () -> Unit,
)

@Composable
private fun StaticCalculatorGrid(
    onChar: (Char) -> Unit,
    onOperation: (Operation) -> Unit,
    onEquals: () -> Unit,
    onBackspace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier) {
        val cellWidth =
            (maxWidth - SPACE_BETWEEN_BUTTONS.dp * (BUTTONS_COLUMNS - 1)) / BUTTONS_COLUMNS
        val cellHeight = (maxHeight - SPACE_BETWEEN_BUTTONS.dp * (BUTTONS_ROWS - 1)) / BUTTONS_ROWS

        @Composable
        fun GridButton(
            row: Int,
            col: Int,
            text: String,
            rowSpan: Int = 1,
            colSpan: Int = 1,
            onClick: () -> Unit = {},
        ) {
            val width = (cellWidth * colSpan) + (SPACE_BETWEEN_BUTTONS.dp * (colSpan - 1))
            val height = (cellHeight * rowSpan) + (SPACE_BETWEEN_BUTTONS.dp * (rowSpan - 1))
            Button(
                onClick = onClick,
                modifier = Modifier
                    .offset(
                        x = (cellWidth + SPACE_BETWEEN_BUTTONS.dp) * col,
                        y = (cellHeight + SPACE_BETWEEN_BUTTONS.dp) * row,
                    )
                    .size(width, height),
                shape = RoundedCornerShape(BUTTON_CORNERS.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
            ) { Text(text) }
        }

        @Composable
        fun GridCharButton(
            row: Int,
            col: Int,
            char: Char,
            colSpan: Int = 1,
        ) = GridButton(row = row, col = col, text = "$char", colSpan = colSpan) { onChar(char) }

        @Composable
        fun GridOperationButton(
            row: Int,
            col: Int,
            operation: Operation,
            rowSpan: Int = 1,
        ) = GridButton(
            row = row,
            col = col,
            text = "${operation.charSymbol}",
            rowSpan = rowSpan,
        ) { onOperation(operation) }

        GridOperationButton(row = 0, col = 0, operation = Operation.Mult)
        GridOperationButton(row = 0, col = 1, operation = Operation.Div)
        GridOperationButton(row = 0, col = 2, operation = Operation.Minus)
        GridButton(row = 0, col = 3, text = "←", onClick = onBackspace)
        GridCharButton(row = 1, col = 0, char = '7')
        GridCharButton(row = 1, col = 1, char = '8')
        GridCharButton(row = 1, col = 2, char = '9')
        GridOperationButton(row = 1, col = 3, rowSpan = 2, operation = Operation.Plus)
        GridCharButton(row = 2, col = 0, char = '4')
        GridCharButton(row = 2, col = 1, char = '5')
        GridCharButton(row = 2, col = 2, char = '6')
        GridCharButton(row = 3, col = 0, char = '1')
        GridCharButton(row = 3, col = 1, char = '2')
        GridCharButton(row = 3, col = 2, char = '3')
        GridButton(row = 3, col = 3, rowSpan = 2, text = "=", onClick = onEquals)
        GridCharButton(row = 4, col = 0, colSpan = 2, char = '0')
        GridCharButton(row = 4, col = 2, char = '.')
    }
}

@Preview
@Composable
private fun RootScreenPreview() {
    RootScreen(
        component = object : CalculationComponent {
            override val calculations = MutableStateFlow(value = emptyList<DomainCalculation>())
            override val calculation = MutableStateFlow(value = Calculation())
            override val currentInput = MutableStateFlow(value = "0.0")
            override val lastSavedId = MutableStateFlow(value = null)
            override val hasNext = MutableStateFlow(value = false)
            override val hasPrevious = MutableStateFlow(value = false)

            override fun appendNumberChar(digit: Char) {}
            override fun applyOperation(operation: Operation) {}
            override fun calculateResult() {}
            override fun backspace() {}
            override fun clearCurrent() {}
            override fun loadNext() {}
            override fun loadPrevious() {}
        },
        modifier = Modifier.fillMaxSize(),
    )
}
