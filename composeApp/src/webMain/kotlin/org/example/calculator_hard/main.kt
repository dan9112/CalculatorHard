package org.example.calculator_hard

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import org.example.calculator_hard.presentation.App
import org.example.calculator_hard.shared.startKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin()

    ComposeViewport {
        App()
    }
}
