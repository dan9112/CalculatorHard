package org.example.calculator_hard

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.example.calculator_hard.presentation.App
import org.example.calculator_hard.shared.startKoin

fun main() {
    startKoin()
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Calculator Hard",
        ) {
            App()
        }
    }
}
