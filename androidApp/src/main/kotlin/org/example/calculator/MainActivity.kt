package org.example.calculator

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import org.example.calculator.presentation.PADDINGS
import org.example.calculator.presentation.RootScreen
import org.example.calculator.presentation.ThemeAttributeValue.Idle
import org.example.calculator.presentation.ThemeAttributeValue.Value
import org.example.calculator.presentation.VerticalPadding
import org.example.calculator.presentation.ui.theme.AppTheme
import org.example.calculator.presentation.ui.theme.ContrastLevel
import org.example.calculator.presentation.ui.theme.isSystemDark
import org.example.calculator.shared.createRootComponent
import java.lang.System.currentTimeMillis

class MainActivity : AppCompatActivity() {
    // todo: figure out why animation ignores its duration time!
    private var startTime = -1L

    override fun onSaveInstanceState(outState: Bundle) = super.onSaveInstanceState(
        outState.apply {
            putLong(START_TIME_FLAG_KEY, startTime)
        },
    )

    private companion object {
        const val START_TIME_FLAG_KEY = "start"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen().apply {
            if (startTime == -1L) startTime = currentTimeMillis()
        }
        savedInstanceState?.let {
            startTime = it.getLong(START_TIME_FLAG_KEY, -1)
        }

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val rootComponent = createRootComponent(componentContext = defaultComponentContext())
        splashScreen.setKeepOnScreenCondition {
            rootComponent
                .settingsComponent
                .run {
                    theme.value == Idle || contrastLevel.value == Idle || (dynamic != null && dynamic!!.value == Idle) ||
                        startTime == -1L || currentTimeMillis() - startTime < 3200L
                }
        }

        setContent {
            val theme by rootComponent.settingsComponent.theme.collectAsState()
            val currentTheme = theme
            val contrast by rootComponent.settingsComponent.contrastLevel.collectAsState()
            val currentContrast = contrast
            val dynamic by rootComponent.settingsComponent.dynamic!!.collectAsState()
            val currentDynamic = dynamic

            val layoutDirection = LocalLayoutDirection.current

            AppTheme(
                darkTheme = (theme as? Value)
                    ?.value
                    ?: isSystemDark(),
                contrastLevel = (contrast as? Value)
                    ?.value
                    ?: ContrastLevel.Normal,
                dynamicColor = (dynamic as? Value)
                    ?.value
                    ?: false,
            ) {
                Scaffold(containerColor = MaterialTheme.colorScheme.primaryContainer) { innerPadding ->
                    val padding = innerPadding + PaddingValues(all = PADDINGS.dp)

                    if (currentTheme is Value && currentContrast is Value && currentDynamic is Value) {
                        RootScreen(
                            component = rootComponent.calculationComponent,
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
                            updateTheme = rootComponent.settingsComponent::updateTheme,
                            contrast = currentContrast.value,
                            updateContrast = rootComponent.settingsComponent::updateContrastLevel,
                            dynamic = currentDynamic.value,
                            updateDynamic = rootComponent.settingsComponent::updateDynamic,
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
