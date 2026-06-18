package org.example.calculator

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.plus
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.arkivanov.decompose.defaultComponentContext
import org.example.calculator.presentation.PADDINGS
import org.example.calculator.presentation.RootComponent
import org.example.calculator.presentation.RootScreen
import org.example.calculator.presentation.ThemeAttributeValue.Value
import org.example.calculator.presentation.VerticalPadding
import org.example.calculator.presentation.ui.theme.AppTheme
import org.example.calculator.presentation.ui.theme.ContrastLevel
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

            var isAnimationFinished by remember { mutableStateOf(value = false) }

            val layoutDirection = LocalLayoutDirection.current

            AppTheme(
                darkTheme = (theme as? Value)
                    ?.value
                    ?: isSystemInDarkTheme(),
                contrastLevel = (contrast as? Value)
                    ?.value
                    ?: ContrastLevel.Normal,
                dynamicColor = (dynamic as? Value)
                    ?.value
                    ?: false
            ) {
                Scaffold(containerColor = MaterialTheme.colorScheme.primaryContainer) { innerPadding ->
                    val padding = innerPadding + PaddingValues(all = PADDINGS.dp)

                    if (currentTheme is Value && currentContrast is Value && currentDynamic is Value && isAnimationFinished) {
                        RootScreen(
                            component = rootComponent.calculationComponent,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    start = padding.calculateStartPadding(layoutDirection),
                                    end = padding.calculateEndPadding(layoutDirection)
                                ),
                            padding = VerticalPadding(
                                top = padding.calculateTopPadding(),
                                bottom = padding.calculateBottomPadding()
                            ),
                            theme = currentTheme.value,
                            updateTheme = rootComponent.settingsComponent::updateTheme,
                            contrast = currentContrast.value,
                            updateContrast = rootComponent.settingsComponent::updateContrastLevel,
                            dynamic = currentDynamic.value,
                            updateDynamic = rootComponent.settingsComponent::updateDynamic
                        )
                    } else {
                        // todo: replace with SplashScreen API later!
                        val currentProgress by animateFloatAsState(
                            targetValue = splashScreenFinished,
                            animationSpec = tween(
                                durationMillis = RootComponent.step.inWholeMilliseconds.toInt(),
                                easing = LinearEasing
                            ),
                        ) { if (it >= 1f) isAnimationFinished = true }

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = when (currentProgress) {
                                        0f -> Brush.linearGradient(
                                            listOf(
                                                Color.Green,
                                                Color.Blue
                                            )
                                        )

                                        1f -> Brush.linearGradient(
                                            listOf(
                                                Color.Red,
                                                Color.Green
                                            )
                                        )

                                        else -> Brush.linearGradient(
                                            0f to Color.Red,
                                            currentProgress to Color.Green,
                                            1f to Color.Blue,
                                        )
                                    }
                                )
                        )
                    }
                }
            }
        }
    }
}

// @Preview
// @Composable
// fun AppAndroidPreview() {
//
// }
