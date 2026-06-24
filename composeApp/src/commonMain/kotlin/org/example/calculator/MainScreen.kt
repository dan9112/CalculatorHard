package org.example.calculator

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import org.example.calculator.presentation.CalculationComponent
import org.example.calculator.presentation.PADDINGS
import org.example.calculator.presentation.RootComponent.Companion.step
import org.example.calculator.presentation.RootScreen
import org.example.calculator.presentation.SettingsComponent
import org.example.calculator.presentation.ThemeAttributeValue.Value
import org.example.calculator.presentation.VerticalPadding
import org.example.calculator.presentation.rememberOrientation
import org.example.calculator.presentation.ui.theme.AppTheme
import org.example.calculator.presentation.ui.theme.ContrastLevel
import org.example.calculator.presentation.ui.theme.isSystemDark

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

    var isAnimationFinished by remember { mutableStateOf(value = false) }

    val layoutDirection = LocalLayoutDirection.current

    AppTheme(
        darkTheme = (theme as? Value)
            ?.value
            ?: isSystemDark(),
        contrastLevel = (contrast as? Value)
            ?.value
            ?: ContrastLevel.Normal,
        dynamicColor = false,
    ) {
        Scaffold(
            modifier = modifier,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ) { innerPadding ->
            val padding = innerPadding + PaddingValues(all = PADDINGS.dp)

            if (currentTheme is Value && currentContrast is Value && isAnimationFinished) {
                RootScreen(
                    component = calculationComponent,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            start = padding.calculateStartPadding(layoutDirection),
                            end = padding.calculateEndPadding(layoutDirection),
                        ),
                    padding = VerticalPadding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding(),
                    ),
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
    }
}
