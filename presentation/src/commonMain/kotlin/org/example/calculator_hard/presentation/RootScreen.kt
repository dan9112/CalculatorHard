package org.example.calculator_hard.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
const val MIN_BUTTONS_WIDTH = MIN_BUTTON_SIZE * 4 + SPACE_BETWEEN_BUTTONS * 3
const val MIN_BUTTONS_HEIGHT = MIN_BUTTON_SIZE * 5 + SPACE_BETWEEN_BUTTONS * 4

private const val BUTTONS_HORIZONTAL_WEIGHT = 5
private const val BUTTONS_VERTICAL_WEIGHT = 4

private const val BUTTON_CORNERS = 10

const val PADDINGS = 5

const val MIN_SCREEN_HORIZONTAL_WIDTH = MIN_DISPLAY_WIDTH + MIN_BUTTONS_WIDTH + PADDINGS * 2
val MIN_SCREEN_HORIZONTAL_HEIGHT = maxOf(MIN_DISPLAY_WIDTH, MIN_BUTTONS_WIDTH) + PADDINGS * 2

val MIN_SCREEN_VERTICAL_WIDTH = maxOf(MIN_DISPLAY_HEIGHT, MIN_BUTTONS_HEIGHT) + PADDINGS * 2
const val MIN_SCREEN_VERTICAL_HEIGHT = MIN_DISPLAY_HEIGHT + MIN_BUTTONS_HEIGHT + PADDINGS * 2

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

                    when {
                        numbers.isEmpty() -> {
                            append('0')
                        }

                        else -> {
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
                        }
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
                },
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth(),
                color = Color.White,
                textAlign = TextAlign.End
            )
        }

        // Buttons
        Column(
            modifier = Modifier
                .width(buttonsWidth)
                .height(buttonsHeight)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
        ) {
            @Composable
            fun TemplateItem(
                modifier: Modifier,
                text: String,
                duplicatedSize: Boolean?,
                onClick: () -> Unit
            ) = Button(
                onClick = onClick,
                modifier = modifier
                    .run {
                        when (duplicatedSize) {
                            true -> sizeIn(
                                minWidth = (MIN_BUTTON_SIZE * 2 + SPACE_BETWEEN_BUTTONS).dp,
                                minHeight = MIN_BUTTON_SIZE.dp
                            )

                            false -> sizeIn(
                                minWidth = MIN_BUTTON_SIZE.dp,
                                minHeight = (MIN_BUTTON_SIZE * 2 + SPACE_BETWEEN_BUTTONS).dp
                            )

                            null -> sizeIn(
                                minWidth = MIN_BUTTON_SIZE.dp,
                                minHeight = MIN_BUTTON_SIZE.dp
                            )
                        }
                    },
                shape = RoundedCornerShape(BUTTON_CORNERS.dp),
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text(text)
            }

            @Composable
            fun ColumnScope.TemplateItem(
                modifier: Modifier = Modifier,
                text: String,
                duplicatedSize: Boolean? = null,
                onClick: () -> Unit
            ) = TemplateItem(
                modifier = modifier
                    .weight(1f)
                    .fillMaxWidth(),
                text = text,
                duplicatedSize = duplicatedSize,
                onClick = onClick
            )

            @Composable
            fun RowScope.TemplateItem(
                modifier: Modifier = Modifier,
                text: String,
                duplicatedSize: Boolean? = null,
                onClick: () -> Unit
            ) = TemplateItem(
                modifier = modifier
                    .weight(1f)
                    .fillMaxHeight(),
                text = text,
                duplicatedSize = duplicatedSize,
                onClick = onClick
            )

            fun addOperation(operation: Operation) {
                rootComponent.addNumber(
                    number = currentNumber
                        .ifEmpty { "0" }
                        .toFloat()
                )
                currentNumber = ""
                rootComponent.addOperation(operation)
            }


            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
            ) {
                TemplateItem(modifier = Modifier.fillMaxHeight(), text = "*") {
                    addOperation(operation = Operation.Mult)
                }
                TemplateItem(modifier = Modifier.fillMaxHeight(), text = "/") {
                    addOperation(operation = Operation.Div)
                }
                TemplateItem(modifier = Modifier.fillMaxHeight(), text = "-") {
                    addOperation(operation = Operation.Minus)
                }
                TemplateItem(modifier = Modifier.fillMaxHeight(), text = "←") {
                    currentNumber = if (currentNumber.isNotEmpty()) {
                        currentNumber.dropLast(1)
                    } else {
                        rootComponent
                            .removeLastSegment()
                            .toString()
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
                ) {
                    TemplateItem(text = "7") {
                        currentNumber += 7
                    }
                    TemplateItem(text = "4") {
                        currentNumber += 4
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
                ) {
                    TemplateItem(text = "8") {
                        currentNumber += 8
                    }
                    TemplateItem(text = "5") {
                        currentNumber += 5
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
                ) {
                    TemplateItem(text = "9") {
                        currentNumber += 9
                    }
                    TemplateItem(text = "6") {
                        currentNumber += 6
                    }
                }
                TemplateItem(text = "+", duplicatedSize = false) {
                    addOperation(operation = Operation.Plus)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(2f)
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
            ) {
                Column(
                    modifier = Modifier.weight(2f),
                    verticalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
                    ) {
                        TemplateItem(modifier = Modifier.fillMaxHeight(), text = "1") {
                            currentNumber += 1
                        }
                        TemplateItem(modifier = Modifier.fillMaxHeight(), text = "2") {
                            currentNumber += 2
                        }
                    }
                    TemplateItem(text = "0", duplicatedSize = true) {
                        currentNumber += 0
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
                ) {
                    TemplateItem(text = "3") {
                        currentNumber += 3
                    }
                    TemplateItem(text = ".") {
                        currentNumber += "."
                    }
                }
                TemplateItem(text = "=", duplicatedSize = false) {
                    rootComponent.addNumber(currentNumber.toFloat())
                    currentNumber = ""
                    rootComponent.finishCalculation()
                }
            }
        }
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
