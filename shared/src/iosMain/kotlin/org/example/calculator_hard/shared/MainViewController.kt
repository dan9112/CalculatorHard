package org.example.calculator_hard.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import org.example.calculator_hard.presentation.RootComponent
import org.example.calculator_hard.presentation.RootScreen
import org.example.calculator_hard.presentation.ui.theme.AppTheme

fun MainViewController(rootComponent: RootComponent) = ComposeUIViewController {
    startKoin()

    AppTheme {
        RootScreen(
            modifier = Modifier.fillMaxSize(),
            rootComponent = rootComponent
        )
    }
}
