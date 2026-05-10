package org.example.calculator_hard

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import org.example.calculator_hard.presentation.MIN_SCREEN_HORIZONTAL_HEIGHT
import org.example.calculator_hard.presentation.MIN_SCREEN_HORIZONTAL_WIDTH
import org.example.calculator_hard.presentation.MIN_SCREEN_VERTICAL_HEIGHT
import org.example.calculator_hard.presentation.MIN_SCREEN_VERTICAL_WIDTH

@Composable
fun SizeController(orientation: Orientation, newMinSizes: (widthPx: Int, heightPx: Int) -> Unit) {
    val density = LocalDensity.current

    LaunchedEffect(orientation, density) {
        fun Int.dpRoundToPx(): Int = density.run {
            this@dpRoundToPx
                .dp
                .roundToPx()
        }

        newMinSizes(
            when (orientation) {
                Orientation.Vertical -> MIN_SCREEN_VERTICAL_WIDTH
                Orientation.Horizontal -> MIN_SCREEN_HORIZONTAL_WIDTH
            }
                .dpRoundToPx(),
            when (orientation) {
                Orientation.Vertical -> MIN_SCREEN_VERTICAL_HEIGHT
                Orientation.Horizontal -> MIN_SCREEN_HORIZONTAL_HEIGHT
            }
                .dpRoundToPx()
        )
    }
}
