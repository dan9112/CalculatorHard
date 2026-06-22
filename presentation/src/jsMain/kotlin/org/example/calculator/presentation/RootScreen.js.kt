package org.example.calculator.presentation

import kotlinx.browser.window

internal actual fun ContextItemScope.copyToClipboard(text: String) {
    window.navigator.clipboard.writeText(text)
}
