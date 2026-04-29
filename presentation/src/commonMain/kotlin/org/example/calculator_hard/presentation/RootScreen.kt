package org.example.calculator_hard.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


internal const val MIN_DISPLAY_WIDTH = 200
internal const val MIN_DISPLAY_HEIGHT = 220

private const val DISPLAY_HORIZONTAL_WEIGHT = 3
private const val DISPLAY_VERTICAL_WEIGHT = 3

private const val MIN_BUTTON_SIZE = 48
private const val SPACE_BETWEEN_BUTTONS = 2
internal const val MIN_BUTTONS_WIDTH = MIN_BUTTON_SIZE * 4 + SPACE_BETWEEN_BUTTONS * 3
internal const val MIN_BUTTONS_HEIGHT = MIN_BUTTON_SIZE * 5 + SPACE_BETWEEN_BUTTONS * 4

private const val BUTTONS_HORIZONTAL_WEIGHT = 4
private const val BUTTONS_VERTICAL_WEIGHT = 4

@Preview
@Composable
fun RootScreen() {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .widthIn(min = (MIN_DISPLAY_WIDTH + MIN_BUTTONS_WIDTH).dp)
            .heightIn(min = (MIN_DISPLAY_HEIGHT + MIN_BUTTONS_HEIGHT).dp)
    ) {
        val width = this.maxWidth
        val height = this.maxHeight
        val aspectRatio = width / height
        val targetButtonsWidth = if (aspectRatio < 1) {
            if (width >= MIN_BUTTONS_WIDTH.dp) {
                width
            } else {
                MIN_BUTTONS_WIDTH.dp
            }
        } else {
            if (width / (DISPLAY_HORIZONTAL_WEIGHT + BUTTONS_HORIZONTAL_WEIGHT) * BUTTONS_HORIZONTAL_WEIGHT >= MIN_BUTTONS_WIDTH.dp && width / (DISPLAY_HORIZONTAL_WEIGHT + BUTTONS_HORIZONTAL_WEIGHT) * DISPLAY_HORIZONTAL_WEIGHT >= MIN_DISPLAY_WIDTH.dp) {
                width / (DISPLAY_HORIZONTAL_WEIGHT + BUTTONS_HORIZONTAL_WEIGHT) * BUTTONS_HORIZONTAL_WEIGHT
            } else if (width - width / (DISPLAY_HORIZONTAL_WEIGHT + BUTTONS_HORIZONTAL_WEIGHT) * DISPLAY_HORIZONTAL_WEIGHT >= MIN_BUTTONS_WIDTH.dp) {
                width - width / (DISPLAY_HORIZONTAL_WEIGHT + BUTTONS_HORIZONTAL_WEIGHT) * DISPLAY_HORIZONTAL_WEIGHT
            } else {
                MIN_BUTTONS_WIDTH.dp
            }
        }
        val targetButtonsHeight = if (aspectRatio >= 1) {
            if (height >= MIN_BUTTONS_HEIGHT.dp) {
                height
            } else {
                MIN_BUTTONS_HEIGHT.dp
            }
        } else {
            if (height / (DISPLAY_VERTICAL_WEIGHT + BUTTONS_VERTICAL_WEIGHT) * BUTTONS_VERTICAL_WEIGHT >= MIN_BUTTONS_HEIGHT.dp && height / (DISPLAY_VERTICAL_WEIGHT + BUTTONS_VERTICAL_WEIGHT) * DISPLAY_VERTICAL_WEIGHT >= MIN_DISPLAY_HEIGHT.dp) {
                height / (DISPLAY_VERTICAL_WEIGHT + BUTTONS_VERTICAL_WEIGHT) * BUTTONS_VERTICAL_WEIGHT
            } else if (height - height / (DISPLAY_VERTICAL_WEIGHT + BUTTONS_VERTICAL_WEIGHT) * DISPLAY_VERTICAL_WEIGHT >= MIN_BUTTONS_HEIGHT.dp) {
                height - height / (DISPLAY_VERTICAL_WEIGHT + BUTTONS_VERTICAL_WEIGHT) * DISPLAY_VERTICAL_WEIGHT
            } else {
                MIN_BUTTONS_HEIGHT.dp
            }
        }
        val targetDisplayWidth = if (aspectRatio < 1) {
            if (width >= MIN_DISPLAY_WIDTH.dp) width else MIN_DISPLAY_WIDTH.dp
        } else {
            if (width - targetButtonsWidth >= MIN_DISPLAY_WIDTH.dp) width - targetButtonsWidth else MIN_DISPLAY_WIDTH.dp
        }
        val targetDisplayHeight = if (aspectRatio >= 1) {
            if (height >= MIN_DISPLAY_HEIGHT.dp) height else MIN_DISPLAY_HEIGHT.dp
        } else {
            if (height - targetButtonsHeight >= MIN_DISPLAY_HEIGHT.dp) height - targetButtonsHeight else MIN_DISPLAY_HEIGHT.dp
        }

        val displayWidth by animateDpAsState(targetValue = targetDisplayWidth)
        val displayHeight by animateDpAsState(targetValue = targetDisplayHeight)

        val buttonsWidth by animateDpAsState(targetValue = targetButtonsWidth)
        val buttonsHeight by animateDpAsState(targetValue = targetButtonsHeight)

        // Display
        Box(
            modifier = Modifier
                .width(displayWidth)
                .height(displayHeight)
                .align(Alignment.TopStart)
                .background(Color.Blue)
        )

        // Buttons
        Column(
            modifier = Modifier
                .width(buttonsWidth)
                .height(buttonsHeight)
                .align(Alignment.BottomEnd)
                .background(Color.Red),
            verticalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
        ) {
            @Composable
            fun TemplateItem(modifier: Modifier, number: Int, duplicatedSize: Boolean?) = Button(
                onClick = {},
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
                    }
            ) {
                Text("$number")
            }

            @Composable
            fun ColumnScope.TemplateItem(
                modifier: Modifier = Modifier,
                number: Int,
                duplicatedSize: Boolean? = null
            ) = TemplateItem(
                modifier = modifier
                    .weight(1f)
                    .fillMaxWidth(),
                number = number,
                duplicatedSize = duplicatedSize
            )

            @Composable
            fun RowScope.TemplateItem(
                modifier: Modifier = Modifier,
                number: Int,
                duplicatedSize: Boolean? = null
            ) = TemplateItem(
                modifier = modifier
                    .weight(1f)
                    .fillMaxHeight(),
                number = number,
                duplicatedSize = duplicatedSize
            )


            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
            ) {
                TemplateItem(modifier = Modifier.fillMaxHeight(), number = 0)
                TemplateItem(modifier = Modifier.fillMaxHeight(), number = 1)
                TemplateItem(modifier = Modifier.fillMaxHeight(), number = 2)
                TemplateItem(modifier = Modifier.fillMaxHeight(), number = 3)
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
                    TemplateItem(number = 4)
                    TemplateItem(number = 5)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
                ) {
                    TemplateItem(number = 6)
                    TemplateItem(number = 7)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
                ) {
                    TemplateItem(number = 8)
                    TemplateItem(number = 9)
                }
                TemplateItem(number = 10, duplicatedSize = false)
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
                        TemplateItem(modifier = Modifier.fillMaxHeight(), number = 11)
                        TemplateItem(modifier = Modifier.fillMaxHeight(), number = 12)
                    }
                    TemplateItem(number = 13, duplicatedSize = true)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(space = SPACE_BETWEEN_BUTTONS.dp)
                ) {
                    TemplateItem(number = 14)
                    TemplateItem(number = 15)
                }
                TemplateItem(number = 16, duplicatedSize = false)
            }
        }
    }
}
