package org.example.calculator_hard.presentation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo

@Composable
actual fun rememberOrientation(): Orientation {
    val size = LocalWindowInfo.current.containerSize
    return if (size.width > size.height) Orientation.Horizontal else Orientation.Vertical
}
