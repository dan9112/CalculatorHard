package org.example.calculator.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import org.example.calculator.domain.Operation
import org.example.calculator.presentation.CalculationResult.Result
import org.example.calculator.presentation.settings.ExpandableSettingsPanel
import org.example.calculator.presentation.ui.theme.AppTheme
import org.example.calculator.presentation.ui.theme.ContrastLevel
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

@Composable
expect fun rememberOrientation(): Orientation

fun Double.formatDisplay() = formatWithPrecision(this, factor = 1_000_000L, precision = 6)
fun Float.formatDisplay() = when {
    isNaN() -> "NaN"
    this == Float.POSITIVE_INFINITY -> "∞"
    this == Float.NEGATIVE_INFINITY -> "-∞"
    else -> formatWithPrecision(toString().toDouble(), factor = 100L, precision = 2)
}

private fun formatWithPrecision(value: Double, factor: Long, precision: Int): String {
    if (value.isNaN()) return "NaN"
    if (value == Double.POSITIVE_INFINITY) return "∞"
    if (value == Double.NEGATIVE_INFINITY) return "-∞"
    val totalUnits = (value * factor).roundToLong().absoluteValue
    val intPart = totalUnits / factor
    val fracPart = totalUnits % factor
    if (intPart == 0L && fracPart == 0L) return "0"
    val sign = if (value < 0) "-" else ""
    if (fracPart == 0L) return "$sign$intPart"
    val fracStr = fracPart.toString().padStart(precision, '0').trimEnd('0')
    return "$sign$intPart.$fracStr"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun RootScreen(
    component: CalculationComponent,
    modifier: Modifier = Modifier,
    theme: Boolean? = null,
    updateTheme: (Boolean?) -> Unit = { },
    contrast: ContrastLevel = ContrastLevel.Normal,
    updateContrast: (ContrastLevel) -> Unit = { },
    dynamic: Boolean = false,
    updateDynamic: ((Boolean) -> Unit)? = null,
    orientation: Orientation = rememberOrientation(),
) {
    AppTheme(
        darkTheme = theme ?: isSystemInDarkTheme(),
        contrastLevel = contrast,
        dynamicColor = dynamic,
    ) {
        Scaffold(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) { innerPadding ->
            val padding = innerPadding + PaddingValues(all = PADDINGS.dp)
            val layoutDirection = LocalLayoutDirection.current

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = padding.calculateStartPadding(layoutDirection),
                        end = padding.calculateEndPadding(layoutDirection),
                    )
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            when (event.key) {
                                Key.NumPad0, Key.Zero -> component.appendDigit("0")

                                Key.NumPad1, Key.One -> component.appendDigit("1")

                                Key.NumPad2, Key.Two -> component.appendDigit("2")

                                Key.NumPad3, Key.Three -> component.appendDigit("3")

                                Key.NumPad4, Key.Four -> component.appendDigit("4")

                                Key.NumPad5, Key.Five -> component.appendDigit("5")

                                Key.NumPad6, Key.Six -> component.appendDigit("6")

                                Key.NumPad7, Key.Seven -> component.appendDigit("7")

                                Key.NumPad8, Key.Eight -> component.appendDigit("8")

                                Key.NumPad9, Key.Nine -> component.appendDigit("9")

                                Key.Period, Key.Comma, Key.NumPadComma, Key.NumPadDot -> component.appendDigit(
                                    ".",
                                )

                                Key.Minus -> component.applyOperation(Operation.Minus)

                                Key.Plus, Key.NumPadAdd -> component.applyOperation(Operation.Plus)

                                Key.Multiply, Key.NumPadMultiply -> component.applyOperation(
                                    Operation.Mult,
                                )

                                Key.Slash, Key.NumPadDivide -> component.applyOperation(
                                    Operation.Div,
                                )

                                Key.Backspace -> component.backspace()

                                Key.Enter, Key.NumPadEnter, Key.NumPadEquals, Key.Equals -> component.calculateResult()

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
                    maxHeight - padding.calculateTopPadding() - padding.calculateBottomPadding() - when (orientation) {
                        Orientation.Vertical -> PADDINGS.dp
                        Orientation.Horizontal -> 0.dp
                    }

                val targetDisplayWidth = when (orientation) {
                    Orientation.Horizontal -> (width * DISPLAY_HORIZONTAL_WEIGHT / (DISPLAY_HORIZONTAL_WEIGHT + BUTTONS_HORIZONTAL_WEIGHT))
                        .coerceAtMost(maximumValue = width - MIN_BUTTONS_WIDTH.dp)

                    Orientation.Vertical -> maxWidth
                }
                val targetDisplayHeight = when (orientation) {
                    Orientation.Horizontal -> maxHeight - padding.calculateBottomPadding()

                    Orientation.Vertical -> (height * DISPLAY_VERTICAL_WEIGHT / (DISPLAY_VERTICAL_WEIGHT + BUTTONS_VERTICAL_WEIGHT))
                        .coerceAtMost(maximumValue = height - MIN_BUTTONS_HEIGHT.dp) + padding.calculateTopPadding()
                }
                val targetButtonsWidth = when (orientation) {
                    Orientation.Horizontal -> width - targetDisplayWidth
                    Orientation.Vertical -> maxWidth
                }
                val targetButtonsHeight = when (orientation) {
                    Orientation.Horizontal -> maxHeight - padding.calculateTopPadding() - padding.calculateBottomPadding()
                    Orientation.Vertical -> height - targetDisplayHeight + padding.calculateTopPadding()
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
                    calculation.operations.forEachIndexed { i, op ->
                        append("${calculation.numbers[i].formatDisplay()} ")
                        append(
                            when (op) {
                                Operation.Plus -> '+'
                                Operation.Minus -> '-'
                                Operation.Mult -> '*'
                                Operation.Div -> '/'
                            },
                        )
                        append(" ")
                    }
                    if (isSticky) {
                        val lastNum =
                            if (calculation.numbers.size > calculation.operations.size) calculation.numbers.last() else 0f
                        append("${lastNum.formatDisplay()} = ${(calculation.result as? Result)?.number?.formatDisplay() ?: "Calculate error"} ")
                    } else {
                        if (calculation.numbers.size > calculation.operations.size) {
                            append(
                                calculation
                                    .numbers
                                    .last()
                                    .formatDisplay(),
                            )
                        } else if (currentInput.isNotEmpty()) {
                            append(currentInput)
                        } else if (calculation.numbers.isEmpty()) {
                            append("0")
                        }
                    }
                }

                val previewResult = if (isSticky) {
                    (calculation.result as? Result)?.number?.formatDisplay()
                        ?: "Error"
                } else {
                    calculatePreview(calculation.numbers, calculation.operations, currentInput)
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .width(displayWidth)
                        .height(displayHeight)
                        .padding(horizontal = 6.dp, vertical = 4.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        state = listState,
                        contentPadding = PaddingValues(
                            top = padding.calculateTopPadding(),
                        ),
                        reverseLayout = true,
                        verticalArrangement = Arrangement.spacedBy(
                            2.dp,
                            alignment = Alignment.Bottom,
                        ),
                        horizontalAlignment = Alignment.End,
                    ) {
                        items(visibleHistory, key = { it.id }) { calc ->
                            Text(
                                text = buildAnnotatedString {
                                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.error)) {
                                        append("${calc.id} ")
                                    }
                                    calc.operations.forEachIndexed { index, op ->
                                        append("${calc.numbers[index].formatDisplay()}  ")
                                        append(
                                            when (op) {
                                                Operation.Plus -> "+ "
                                                Operation.Minus -> "- "
                                                Operation.Mult -> "* "
                                                Operation.Div -> "/ "
                                            },
                                        )
                                    }
                                    calc.numbers.lastOrNull()?.let { lastNum ->
                                        append("${lastNum.formatDisplay()} = ${calc.result?.formatDisplay() ?: "Error"} ")
                                    }
                                },
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = 0.6f,
                                    ),
                                ),
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
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textAlign = TextAlign.End,
                    )
                }

                StaticCalculatorGrid(
                    modifier = Modifier
                        .padding(
                            bottom = padding.calculateBottomPadding(),
                        )
                        .width(buttonsWidth)
                        .height(buttonsHeight)
                        .align(Alignment.BottomEnd),
                    onDigit = component::appendDigit,
                    onOperation = component::applyOperation,
                    onEquals = component::calculateResult,
                    onBackspace = component::backspace,
                )

                ExpandableSettingsPanel(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = padding.calculateTopPadding()),
                    buttonShape = RoundedCornerShape(size = BUTTON_CORNERS.dp),
                    theme = theme to updateTheme,
                    contrast = contrast to updateContrast,
                    dynamic = updateDynamic?.let { update -> dynamic to update },
                )
            }
        }
    }
}

private fun calculatePreview(
    numbers: List<Float>,
    operations: List<Operation>,
    currentInput: String,
): String {
    if (currentInput.isEmpty() && numbers.isEmpty()) return "0"
    val currentNum = currentInput.toFloatOrNull() ?: 0f
    val allNumbers = if (currentInput.isNotEmpty()) {
        if (numbers.size > operations.size) numbers.dropLast(1) + currentNum else numbers + currentNum
    } else {
        numbers
    }
    if (allNumbers.isEmpty()) return "0"
    if (allNumbers.size == 1 && operations.isEmpty()) return allNumbers[0].formatDisplay()

    val nums = allNumbers.toMutableList()
    val ops = operations.toMutableList()
    var idx = 0
    while (idx < ops.size) {
        when (ops[idx]) {
            Operation.Mult -> {
                nums[idx] *= nums[idx + 1]
                nums.removeAt(idx + 1)
                ops.removeAt(idx)
            }

            Operation.Div -> {
                if (nums[idx + 1] == 0f) return "∞"
                nums[idx] /= nums[idx + 1]
                nums.removeAt(idx + 1)
                ops.removeAt(
                    idx,
                )
            }

            else -> idx++
        }
    }
    return ops.zip(nums.drop(1))
        .fold(nums.first()) { acc, (op, n) -> if (op == Operation.Plus) acc + n else acc - n }
        .formatDisplay()
}

@Composable
private fun StaticCalculatorGrid(
    onDigit: (String) -> Unit,
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

        GridButton(row = 0, col = 0, text = "*") { onOperation(Operation.Mult) }
        GridButton(row = 0, col = 1, text = "/") { onOperation(Operation.Div) }
        GridButton(row = 0, col = 2, text = "-") { onOperation(Operation.Minus) }
        GridButton(row = 0, col = 3, text = "←") { onBackspace() }
        GridButton(row = 1, col = 0, text = "7") { onDigit("7") }
        GridButton(row = 1, col = 1, text = "8") { onDigit("8") }
        GridButton(row = 1, col = 2, text = "9") { onDigit("9") }
        GridButton(row = 1, col = 3, rowSpan = 2, text = "+") { onOperation(Operation.Plus) }
        GridButton(row = 2, col = 0, text = "4") { onDigit("4") }
        GridButton(row = 2, col = 1, text = "5") { onDigit("5") }
        GridButton(row = 2, col = 2, text = "6") { onDigit("6") }
        GridButton(row = 3, col = 0, text = "1") { onDigit("1") }
        GridButton(row = 3, col = 1, text = "2") { onDigit("2") }
        GridButton(row = 3, col = 2, text = "3") { onDigit("3") }
        GridButton(row = 3, col = 3, rowSpan = 2, text = "=") { onEquals() }
        GridButton(row = 4, col = 0, colSpan = 2, text = "0") { onDigit("0") }
        GridButton(row = 4, col = 2, text = ".") { onDigit(".") }
    }
}

@Preview
@Composable
private fun RootScreenPreview() {
    RootScreen(
        component = object : CalculationComponent {
            override val calculations = MutableStateFlow(emptyList<DomainCalculation>())
            override val calculation = MutableStateFlow(Calculation())
            override val currentInput = MutableStateFlow("0.0")
            override val lastSavedId = MutableStateFlow(null)
            override val hasNext = MutableStateFlow(false)
            override val hasPrevious = MutableStateFlow(false)

            override fun appendDigit(digit: String) {}
            override fun applyOperation(operation: Operation) {}
            override fun calculateResult() {}
            override fun backspace() {}
            override fun clearCurrent() {}
            override fun loadNext() {}
            override fun loadPrevious() {}
        },
    )
}
