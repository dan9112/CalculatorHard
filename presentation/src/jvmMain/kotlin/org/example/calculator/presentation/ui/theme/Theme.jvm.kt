package org.example.calculator.presentation.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jthemedetecor.OsThemeDetector
import java.util.function.Consumer

@Composable
actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null

@Composable
actual fun isSystemDark(): Boolean {
    val detector = remember { OsThemeDetector.getDetector() }
    var isDark by remember { mutableStateOf(value = detector.isDark) }

    DisposableEffect(Unit) {
        val listener = Consumer { dark: Boolean -> isDark = dark }
        detector.registerListener(listener)
        onDispose {
            detector.removeListener(listener)
        }
    }
    return isDark
}
