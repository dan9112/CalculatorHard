package org.example.calculator_hard.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

val MIN_SCREEN_HORIZONTAL_WIDTH = MIN_DISPLAY_WIDTH + MIN_BUTTONS_WIDTH + PADDINGS * 2
val MIN_SCREEN_HORIZONTAL_HEIGHT = maxOf(MIN_DISPLAY_WIDTH, MIN_BUTTONS_WIDTH) + PADDINGS * 2

val MIN_SCREEN_VERTICAL_WIDTH = maxOf(MIN_DISPLAY_HEIGHT, MIN_BUTTONS_HEIGHT) + PADDINGS * 2
val MIN_SCREEN_VERTICAL_HEIGHT = MIN_DISPLAY_HEIGHT + MIN_BUTTONS_HEIGHT + PADDINGS * 2

@Composable
expect fun rememberOrientation(): Orientation

@Composable
fun RootScreen(
    modifier: Modifier = Modifier,
    rootComponent: RootComponent,
    orientation: Orientation = rememberOrientation()
) {
    BoxWithConstraints(
        modifier = modifier
            .background(color = Color.DarkGray)
            .safeDrawingPadding()// todo: replace with normal paddings later!
            .fillMaxSize()
            .padding(all = PADDINGS.dp)
    ) {
        val width = maxWidth
        val height = maxHeight
        val targetDisplayWidth = when (orientation) {
            Orientation.Horizontal -> (width * DISPLAY_HORIZONTAL_WEIGHT / (DISPLAY_HORIZONTAL_WEIGHT + BUTTONS_HORIZONTAL_WEIGHT)).coerceAtMost(
                maximumValue = width - MIN_BUTTONS_WIDTH.dp
            )

            Orientation.Vertical -> width
        }
        val targetDisplayHeight = when (orientation) {
            Orientation.Horizontal -> height
            Orientation.Vertical -> (height * DISPLAY_VERTICAL_WEIGHT / (DISPLAY_VERTICAL_WEIGHT + BUTTONS_VERTICAL_WEIGHT)).coerceAtMost(
                maximumValue = height - MIN_BUTTONS_HEIGHT.dp
            )
        }
        val targetButtonsWidth = when (orientation) {
            Orientation.Horizontal -> {
                width - targetDisplayWidth
            }

            Orientation.Vertical -> {
                width
            }
        }
        val targetButtonsHeight = when (orientation) {
            Orientation.Horizontal -> {
                height
            }

            Orientation.Vertical -> {
                height - targetDisplayHeight
            }
        }

        val displayWidth by animateDpAsState(targetValue = targetDisplayWidth)
        val displayHeight by animateDpAsState(targetValue = targetDisplayHeight)

        val buttonsWidth by animateDpAsState(targetValue = targetButtonsWidth)
        val buttonsHeight by animateDpAsState(targetValue = targetButtonsHeight)

        // Display

        val calculation by rootComponent.calculation.collectAsState()
        LaunchedEffect(calculation) {
            println(calculation.numbers.joinToString())
            println(calculation.operations.joinToString())
        }

        val historyScrollState = rememberLazyListState()
//        val displayContentScrollState = rememberScrollState()

        val nestedScrollConnection = object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                return Offset.Zero
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return Offset(0f, consumed.y)
            }
        }

        var currentNumber by rememberSaveable { mutableStateOf(value = "") }

        Column(
            modifier = Modifier
                .width(displayWidth)
                .height(displayHeight)
                .align(Alignment.TopStart)
                .background(
                    brush = Brush.linearGradient(listOf(Color.Cyan, Color.Magenta, Color.Blue))
                )
                .nestedScroll(nestedScrollConnection)
                .padding(all = 10.dp)
        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                state = historyScrollState,
                reverseLayout = true,
                verticalArrangement = Arrangement.spacedBy(2.dp, alignment = Alignment.Bottom),
                horizontalAlignment = Alignment.End
            ) {

            }
            Text(
                text = buildAnnotatedString {
                    val numbers = calculation.numbers
                    val operations = calculation.operations

                    if (numbers.isEmpty() || calculation.stored && currentNumber.isNotEmpty()) {
                        append(currentNumber)
                        appendLine()
                        append('0')
                    } else {
                        repeat(times = operations.size) {
                            append("${numbers[it]}")
                            append(
                                when (operations[it]) {
                                    Operation.Plus -> '+'
                                    Operation.Minus -> '-'
                                    Operation.Mult -> '*'
                                    Operation.Div -> '/'
                                }
                            )
                        }
                        if (numbers.size > operations.size) {
                            append("${numbers.last()}")
                        } else {
                            append(currentNumber)
                        }
                        appendLine()
                        when (val result = calculation.result) {
                            CalculationResult.DivideByZero -> withStyle(
                                style = SpanStyle(
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("Dividing by zero!")
                            }

                            is CalculationResult.Result -> withStyle(
                                SpanStyle(fontWeight = if (calculation.stored) FontWeight.Bold else FontWeight.Normal)
                            ) {
                                append("${result.number}")
                            }
                        }
                    }
                },
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                color = Color.White,
                textAlign = TextAlign.End
            )
        }

        // Buttons
        StaticCalculatorGrid(
            modifier = Modifier
                .width(buttonsWidth)
                .height(buttonsHeight)
                .align(Alignment.BottomEnd),
            rootComponent = rootComponent
        )
    }
}

@Composable
private fun StaticCalculatorGrid(modifier: Modifier = Modifier, rootComponent: RootComponent) {
    var currentNumber by remember { mutableStateOf("") }

    fun addOperation(operation: Operation) {
        rootComponent.addNumber(
            currentNumber
                .ifEmpty { "0" }
                .toFloat()
        )
        currentNumber = ""
        rootComponent.addOperation(operation)
    }

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


        GridButton(0, 0, text = "*") { addOperation(Operation.Mult) }
        GridButton(0, 1, text = "/") { addOperation(Operation.Div) }
        GridButton(0, 2, text = "-") { addOperation(Operation.Minus) }
        GridButton(0, 3, text = "←") {
            currentNumber = if (currentNumber.isNotEmpty()) {
                currentNumber.dropLast(1)
            } else {
                rootComponent.removeLastSegment().toString()
            }
        }

        GridButton(1, 0, text = "7") { currentNumber += "7" }
        GridButton(1, 1, text = "8") { currentNumber += "8" }
        GridButton(1, 2, text = "9") { currentNumber += "9" }
        GridButton(1, 3, rowSpan = 2, text = "+") { addOperation(Operation.Plus) }

        GridButton(2, 0, text = "4") { currentNumber += "4" }
        GridButton(2, 1, text = "5") { currentNumber += "5" }
        GridButton(2, 2, text = "6") { currentNumber += "6" }

        GridButton(3, 0, text = "1") { currentNumber += "1" }
        GridButton(3, 1, text = "2") { currentNumber += "2" }
        GridButton(3, 2, text = "3") { currentNumber += "3" }
        GridButton(3, 3, rowSpan = 2, text = "=") {
            rootComponent.addNumber(currentNumber.toFloat())
            currentNumber = ""
            rootComponent.finishCalculation()
        }

        GridButton(4, 0, colSpan = 2, text = "0") { currentNumber += "0" }
        GridButton(4, 2, text = ".") { currentNumber += "." }
    }
}


@Composable
@Preview
private fun RootScreenPreview() {
    RootScreen(
        rootComponent = object : RootComponent {
            override val calculation = MutableStateFlow(value = Calculation())
            override fun addOperation(operation: Operation) {

            }

            override fun finishCalculation() {

            }

            override fun addNumber(number: Float) {

            }

            override fun removeLastSegment() = 0f
        }
    )
}
