package org.example.calculator_hard.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

// Функция теперь только рендерит UI, состояние приходит извне
fun MainViewController(rootComponent: RootComponent): UIViewController =
    ComposeUIViewController {
        RootScreen(
            modifier = Modifier.fillMaxSize(),
            rootComponent = rootComponent
        )
    }
