package org.example.calculator.presentation

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(text) => navigator.clipboard.writeText(text)")
private external fun writeTextToClipboard(text: String)

internal actual fun ContextItemScope.copyToClipboard(text: String) {
    writeTextToClipboard(text)
}
