package org.example.calculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.lifecycle.LifecycleController
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.example.calculator.presentation.rememberOrientation
import org.example.calculator.shared.createRootComponent
import org.example.calculator.shared.startKoin
import java.awt.Dimension
import java.awt.Color as AwtColor

fun main() {
    startKoin()

    val lifecycle = LifecycleRegistry()
    val rootComponent = runBlocking(Dispatchers.Main) {
        createRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
    }

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Calculator Hard",
        ) {
            val orientation = rememberOrientation()
            window.background = Color.DarkGray.run {
                AwtColor(red, green, blue, alpha)
            }

            SizeController(orientation) { width, height ->
                window.minimumSize = Dimension(width, height)
            }

            LifecycleController(lifecycleRegistry = lifecycle, windowState = rememberWindowState())

            var needSplashScreen by rememberSaveable { mutableStateOf(value = true) }

            MainScreen(
                modifier = Modifier.fillMaxSize(),
                calculationComponent = rootComponent.calculationComponent,
                settingsComponent = rootComponent.settingsComponent,
                splashScreenFinished = !needSplashScreen,
            )
            if (needSplashScreen) {
                SplashScreen(
                    onFinish = { needSplashScreen = false },
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color(color = 0xFFF7FAFC)),
                )
            }
        }
    }
}
