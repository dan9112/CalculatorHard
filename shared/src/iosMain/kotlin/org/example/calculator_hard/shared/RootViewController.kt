package org.example.calculator_hard.shared

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import org.example.calculator_hard.presentation.RootComponent
import org.example.calculator_hard.presentation.RootScreen
import platform.UIKit.UIViewController

fun rootViewController(root: RootComponent): UIViewController =
    ComposeUIViewController {
        // Ваш корневой Compose-экран
        RootScreen(modifier = Modifier.fillMaxSize(), rootComponent = root)
    }
