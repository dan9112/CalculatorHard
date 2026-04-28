package org.example.calculator_hard.shared

import androidx.compose.ui.window.ComposeUIViewController
import org.example.calculator_hard.presentation.App

fun MainViewController() = ComposeUIViewController {
    startKoin()
    App()
}
