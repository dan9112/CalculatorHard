package org.example.calculator.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import org.example.calculator.presentation.RootComponent
import org.example.calculator.presentation.RootScreen

fun mainViewController(rootComponent: RootComponent) = ComposeUIViewController {
    startKoin()

    RootScreen(
        modifier = Modifier.fillMaxSize(),
        rootComponent = rootComponent,
    )
}
