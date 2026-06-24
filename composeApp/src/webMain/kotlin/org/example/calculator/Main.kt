package org.example.calculator

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import kotlinx.browser.window
import org.example.calculator.presentation.MIN_SCREEN_HORIZONTAL_HEIGHT
import org.example.calculator.presentation.MIN_SCREEN_HORIZONTAL_WIDTH
import org.example.calculator.presentation.MIN_SCREEN_VERTICAL_HEIGHT
import org.example.calculator.presentation.MIN_SCREEN_VERTICAL_WIDTH
import org.example.calculator.shared.createRootComponent
import org.example.calculator.shared.startKoin
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin()
    val lifecycle = LifecycleRegistry()
    val rootComponent =
        createRootComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
    lifecycle.attachToDocument()

    (document.documentElement as? HTMLElement)?.style?.run {
        if (window.innerWidth >= window.innerHeight) {
            minWidth = "${MIN_SCREEN_HORIZONTAL_WIDTH * window.devicePixelRatio}px"
            minHeight = "${MIN_SCREEN_HORIZONTAL_HEIGHT * window.devicePixelRatio}px"
        } else {
            minWidth = "${MIN_SCREEN_VERTICAL_WIDTH * window.devicePixelRatio}px"
            minHeight = "${MIN_SCREEN_VERTICAL_HEIGHT * window.devicePixelRatio}px"
        }

        overflowX = "auto"
        overflowY = "auto"
    }

    ComposeViewport {
        MainScreen(
            modifier = Modifier.fillMaxSize(),
            calculationComponent = rootComponent.calculationComponent,
            settingsComponent = rootComponent.settingsComponent,
            splashScreenFinished = true, // todo: check HTML!
        )
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun getVisibilityState(): String = js("document.visibilityState")

private fun LifecycleRegistry.attachToDocument() {
    val callback: (Event) -> Unit = { _ ->
        if (getVisibilityState() == "visible") resume() else stop()
    }

    if (getVisibilityState() == "visible") resume() else stop()

    document.addEventListener("visibilitychange", callback)
    doOnDestroy { document.removeEventListener("visibilitychange", callback) }
}
