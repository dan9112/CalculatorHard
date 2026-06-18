package org.example.calculator

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import org.example.calculator.presentation.CalculationComponent
import org.example.calculator.presentation.RootComponent.Companion.step
import org.example.calculator.presentation.RootScreen
import org.example.calculator.presentation.SettingsComponent
import org.example.calculator.presentation.ThemeAttributeValue.Value
import org.example.calculator.presentation.rememberOrientation

@Composable
fun MainScreen(
    calculationComponent: CalculationComponent,
    settingsComponent: SettingsComponent,
    splashScreenFinished: Float,
    modifier: Modifier = Modifier,
    orientation: Orientation = rememberOrientation(),
) {
    val theme by settingsComponent.theme.collectAsState()
    val currentTheme = theme
    val contrast by settingsComponent.contrastLevel.collectAsState()
    val currentContrast = contrast

    if (currentTheme is Value && currentContrast is Value && splashScreenFinished >= 1f) {
        RootScreen(
            modifier = modifier,
            component = calculationComponent,
            theme = currentTheme.value,
            updateTheme = settingsComponent::updateTheme,
            contrast = currentContrast.value,
            updateContrast = settingsComponent::updateContrastLevel,
            orientation = orientation,
        )
    } else {
        // todo: splash screen template!
        val currentProgress by animateFloatAsState(
            targetValue = splashScreenFinished,
            animationSpec = tween(
                durationMillis = step.inWholeMilliseconds.toInt(),
                easing = LinearEasing,
            ),
        )
        // todo: splash screen template!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        0f to Color.Red,
                        currentProgress to Color.Green,
                        1f to Color.Blue,
                    ),
                ),
        )
    }
}
