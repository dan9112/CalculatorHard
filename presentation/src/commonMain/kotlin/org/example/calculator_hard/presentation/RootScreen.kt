package org.example.calculator_hard.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.coerceAtMost
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.MutableStateFlow

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

/** Вычисляет результат "на лету" для динамического предпросмотра */
private fun calculatePreview(
    numbers: List<Float>,
    operations: List<Operation>,
    currentNumberStr: String
): String {
    if (currentNumberStr.isEmpty() && numbers.isEmpty()) return "0"
    val currentNum = currentNumberStr.toFloatOrNull() ?: 0f

    val allNumbers = if (currentNumberStr.isNotEmpty()) {
        if (numbers.size > operations.size) {
            numbers.dropLast(1) + currentNum
        } else {
            numbers + currentNum
        }
    } else {
        numbers
    }

    if (allNumbers.isEmpty()) return "0"
    if (allNumbers.size == 1 && operations.isEmpty()) return allNumbers[0].toString()

    val nums = allNumbers.toMutableList()
    val ops = operations.toMutableList()
    if (nums.size == ops.size) nums.add(0f)

    var idx = 0
    while (idx < ops.size) {
        when (ops[idx]) {
            Operation.Mult -> {
                nums[idx] *= nums[idx + 1]
                ops.removeAt(idx)
                nums.removeAt(idx + 1)
            }

            Operation.Div -> {
                if (nums[idx + 1] == 0f) return "∞"
                nums[idx] /= nums[idx + 1]
                ops.removeAt(idx)
                nums.removeAt(idx + 1)
            }

            else -> idx++
        }
    }

    val res = ops.zip(nums.drop(1)).fold(nums.first()) { acc, (op, n) ->
        if (op == Operation.Plus) acc + n else acc - n
    }

    return if (res == res.toLong().toFloat()) res.toLong().toString() else res.toString()
}

@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    rootComponent: RootComponent,
    orientation: Orientation = rememberOrientation()
) {
    Scaffold {
        BoxWithConstraints(modifier = modifier.padding(all = PADDINGS.dp)) {
            val width = maxWidth - PADDINGS.dp
            val height = maxHeight - PADDINGS.dp

            val targetDisplayWidth = when (orientation) {
                Orientation.Horizontal -> (width * DISPLAY_HORIZONTAL_WEIGHT / (DISPLAY_HORIZONTAL_WEIGHT + BUTTONS_HORIZONTAL_WEIGHT)).coerceAtMost(
                    maximumValue = width - MIN_BUTTONS_WIDTH.dp
                )

                Orientation.Vertical -> maxWidth
            }
            val targetDisplayHeight = when (orientation) {
                Orientation.Horizontal -> maxHeight
                Orientation.Vertical -> (height * DISPLAY_VERTICAL_WEIGHT / (DISPLAY_VERTICAL_WEIGHT + BUTTONS_VERTICAL_WEIGHT)).coerceAtMost(
                    maximumValue = height - MIN_BUTTONS_HEIGHT.dp
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
            var currentNumber by rememberSaveable { mutableStateOf(value = "") }

            val onDigit: (String) -> Unit = { digit ->
                if (calculation.stored && currentNumber.isBlank()) {
                    if (digit == ".") currentNumber = "0"
                    currentNumber += digit
                } else {
                    if (currentNumber == "0" && digit != ".") {
                        currentNumber = digit
                    } else {
                        if (currentNumber.contains(".")) {
                            if (currentNumber.length < 7) currentNumber += digit
                        } else {
                            if (digit == ".") {
                                if (currentNumber.isBlank()) currentNumber += "0"
                                currentNumber += digit
                            } else if (currentNumber.length < 4) {
                                currentNumber += digit
                            }
                        }
                    }
                }
            }

            val onOperation: (Operation) -> Unit = { op ->
                rootComponent.addNumber(if (currentNumber.isBlank()) 0f else currentNumber.toFloat())
                rootComponent.addOperation(op)
                currentNumber = ""
            }

            val onEquals: () -> Unit = {
                if (currentNumber.isNotEmpty()) {
                    rootComponent.addNumber(currentNumber.toFloat())
                    rootComponent.finishCalculation()
                    currentNumber = ""
                }
            }

            val onBackspace: () -> Unit = {
                if (calculation.stored && currentNumber.isBlank()) {
                    currentNumber = "0"
                } else {
                    if (currentNumber.isNotEmpty()) {
                        currentNumber = currentNumber.dropLast(1)
                    } else {
                        rootComponent.removeLastSegment()
                    }
                }
            }

            val historyScrollState = rememberLazyListState()
            val nestedScrollConnection = object : NestedScrollConnection {
                override fun onPreScroll(available: Offset, source: NestedScrollSource) =
                    Offset.Zero

                override fun onPostScroll(
                    consumed: Offset,
                    available: Offset,
                    source: NestedScrollSource
                ) = Offset(0f, consumed.y)
            }

            Column(
                modifier = Modifier
                    .width(displayWidth)
                    .height(displayHeight)
                    .align(Alignment.TopStart)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(size = BUTTON_CORNERS.dp)
                    )
                    .nestedScroll(nestedScrollConnection)
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    state = historyScrollState,
                    reverseLayout = true,
                    verticalArrangement = Arrangement.spacedBy(
                        2.dp,
                        alignment = Alignment.Bottom
                    ),
                    horizontalAlignment = Alignment.End
                ) {}

                val numbers = calculation.numbers
                val operations = calculation.operations
                val isNewCalculation = calculation.stored && currentNumber.isNotEmpty()

                val expression = buildString {
                    if (!isNewCalculation) {
                        for (i in operations.indices) {
                            append("${numbers[i]} ")
                            append(
                                when (operations[i]) {
                                    Operation.Plus -> '+'
                                    Operation.Minus -> '-'
                                    Operation.Mult -> '*'
                                    Operation.Div -> '/'
                                }
                            )
                            append(" ")
                        }
                    }
                    append(
                        currentNumber.ifEmpty {
                            if (!isNewCalculation && numbers.size > operations.size) {
                                numbers.last()
                                    .toString()
                            } else {
                                "0"
                            }
                        }
                    )
                }

                val previewResult = calculatePreview(
                    numbers = if (isNewCalculation) emptyList() else numbers,
                    operations = if (isNewCalculation) emptyList() else operations,
                    currentNumberStr = currentNumber
                )

                Text(
                    text = buildAnnotatedString {
                        append(expression)
                        appendLine()
                        withStyle(
                            style = SpanStyle(
                                fontWeight = if (calculation.stored && currentNumber.isBlank()) {
                                    FontWeight.Bold
                                } else {
                                    null
                                }
                            )
                        ) {
                            append(previewResult)
                        }
                    },
                    modifier = Modifier.wrapContentHeight().fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.End
                )
            }

            StaticCalculatorGrid(
                modifier = Modifier
                    .width(buttonsWidth)
                    .height(buttonsHeight)
                    .align(Alignment.BottomEnd),
                onDigit = onDigit,
                onOperation = onOperation,
                onEquals = onEquals,
                onBackspace = onBackspace
            )
        }
    }
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
            (maxWidth - SPACE_BETWEEN_BUTTONS.dp * BUTTONS_COLUMNS.dec()) / BUTTONS_COLUMNS
        val cellHeight = (maxHeight - SPACE_BETWEEN_BUTTONS.dp * BUTTONS_ROWS.dec()) / BUTTONS_ROWS

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
                modifier = Modifier
                    .offset(
                        x = (cellWidth + SPACE_BETWEEN_BUTTONS.dp) * col,
                        y = (cellHeight + SPACE_BETWEEN_BUTTONS.dp) * row
                    )
                    .size(width, height),
                shape = RoundedCornerShape(BUTTON_CORNERS.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(text)
            }
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
            override val calculation = MutableStateFlow(value = Calculation())
            override fun addOperation(operation: Operation) {}
            override fun finishCalculation() {}
            override fun addNumber(number: Float) {}
            override fun removeLastSegment() = 0f
        }
    )
}
