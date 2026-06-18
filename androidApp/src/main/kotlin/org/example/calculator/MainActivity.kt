package org.example.calculator

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.defaultComponentContext
import org.example.calculator.presentation.RootComponent
import org.example.calculator.presentation.RootScreen
import org.example.calculator.presentation.ThemeAttributeValue.Value
import org.example.calculator.shared.createRootComponent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val rootComponent = createRootComponent(componentContext = defaultComponentContext())

        setContent {
            val theme by rootComponent.settingsComponent.theme.collectAsState()
            val currentTheme = theme
            val contrast by rootComponent.settingsComponent.contrastLevel.collectAsState()
            val currentContrast = contrast
            val dynamic by rootComponent.settingsComponent.dynamic!!.collectAsState()
            val currentDynamic = dynamic

            val splashScreenFinished by rootComponent.splashScreenFinished.collectAsState()

            if (currentTheme is Value && currentContrast is Value && currentDynamic is Value && splashScreenFinished >= 1f) {
                RootScreen(
                    modifier = Modifier.fillMaxSize(),
                    component = rootComponent.calculationComponent,
                    theme = currentTheme.value,
                    updateTheme = rootComponent.settingsComponent::updateTheme,
                    contrast = currentContrast.value,
                    updateContrast = rootComponent.settingsComponent::updateContrastLevel,
                    dynamic = currentDynamic.value,
                    updateDynamic = rootComponent.settingsComponent::updateDynamic,
                )
            } else {
                // todo: splash screen template!
                val currentProgress by animateFloatAsState(
                    targetValue = splashScreenFinished,
                    animationSpec = tween(
                        durationMillis = RootComponent.step.inWholeMilliseconds.toInt(),
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
    }
}

// @Preview
// @Composable
// fun AppAndroidPreview() {
//
// }
