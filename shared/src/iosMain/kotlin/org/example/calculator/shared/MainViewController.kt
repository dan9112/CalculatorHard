package org.example.calculator.shared

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.ComposeUIViewController
import org.example.calculator.presentation.RootComponent
import org.example.calculator.presentation.RootComponent.Companion.step
import org.example.calculator.presentation.RootScreen
import org.example.calculator.presentation.ThemeAttributeValue.Value

fun mainViewController(rootComponent: RootComponent) = ComposeUIViewController {
    startKoin()

    val theme by rootComponent.settingsComponent.theme.collectAsState()
    val currentTheme = theme
    val contrast by rootComponent.settingsComponent.contrastLevel.collectAsState()
    val currentContrast = contrast

    val splashScreenFinished by rootComponent.splashScreenFinished.collectAsState()

    var isAnimationFinished by remember { mutableStateOf(value = false) }

    if (currentTheme is Value && currentContrast is Value && isAnimationFinished) {
        RootScreen(
            modifier = Modifier.fillMaxSize(),
            component = rootComponent.calculationComponent,
            theme = currentTheme.value,
            updateTheme = rootComponent.settingsComponent::updateTheme,
            contrast = currentContrast.value,
            updateContrast = rootComponent.settingsComponent::updateContrastLevel,
        )
    } else {
        // todo: splash screen template!
        val currentProgress by animateFloatAsState(
            targetValue = splashScreenFinished,
            animationSpec = tween(
                durationMillis = step.inWholeMilliseconds.toInt(),
                easing = LinearEasing,
            ),
        ) { if (it >= 1f) isAnimationFinished = true }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = when (currentProgress) {
                        0f -> Brush.linearGradient(listOf(Color.Green, Color.Blue))

                        1f -> Brush.linearGradient(listOf(Color.Red, Color.Green))

                        else -> Brush.linearGradient(
                            0f to Color.Red,
                            currentProgress to Color.Green,
                            1f to Color.Blue,
                        )
                    },
                ),
        )
    }
}
