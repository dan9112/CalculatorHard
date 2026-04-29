package org.example.calculator_hard.presentation

import android.content.res.Configuration
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
actual fun rememberOrientation(): Orientation {
    val configuration = LocalConfiguration.current
    return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Orientation.Horizontal
    } else {
        Orientation.Vertical
    }
}
