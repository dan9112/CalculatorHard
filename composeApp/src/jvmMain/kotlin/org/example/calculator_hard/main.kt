package org.example.calculator_hard

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Calculator Hard",
    ) {
        App()
    }
}
