package org.example.calculator

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import org.example.calculator.presentation.CalculationComponent
import org.example.calculator.presentation.PADDINGS
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
    splashScreenFinished: Boolean,
    modifier: Modifier = Modifier,
    orientation: Orientation = rememberOrientation(),
) {
    val theme by settingsComponent.theme.collectAsState()
    val currentTheme = theme
    val contrast by settingsComponent.contrastLevel.collectAsState()
    val currentContrast = contrast

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
            containerColor = if (currentTheme is Value && currentContrast is Value && splashScreenFinished) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                Color(color = 0xFFF7FAFC)
            },
        ) { innerPadding ->
            val padding = innerPadding + PaddingValues(all = PADDINGS.dp)

            if (currentTheme is Value && currentContrast is Value && splashScreenFinished) {
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        imageVector = CalculatorIcon,
                        contentDescription = "Splash screen",
                        modifier = Modifier.size(256.dp),
                    )
                }
            }
        }
    }
}
