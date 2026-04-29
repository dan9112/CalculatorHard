package org.example.calculator_hard

import androidx.compose.foundation.layout.fillMaxSize
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
import org.example.calculator_hard.presentation.RootScreen
import org.example.calculator_hard.presentation.createRootComponent
import org.example.calculator_hard.presentation.rememberOrientation
import org.example.calculator_hard.shared.startKoin
import java.awt.Dimension
import java.awt.Color as AwtColor

fun main() {
    startKoin()

    val lifecycle = LifecycleRegistry()
    val rootComponent = runBlocking(Dispatchers.Main) {
        createRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle)
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
            RootScreen(modifier = Modifier.fillMaxSize(), rootComponent = rootComponent)
        }
    }
}
