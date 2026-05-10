package org.example.calculator_hard.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import org.example.calculator_hard.presentation.RootComponent
import org.example.calculator_hard.presentation.RootScreen

fun MainViewController(rootComponent: RootComponent) = ComposeUIViewController {
    startKoin()

    RootScreen(
        modifier = Modifier.fillMaxSize(),
        rootComponent = rootComponent
    )
}
