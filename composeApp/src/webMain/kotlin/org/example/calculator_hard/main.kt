package org.example.calculator_hard

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.essenty.lifecycle.stop
import kotlinx.browser.document
import org.example.calculator_hard.presentation.*
import org.example.calculator_hard.shared.startKoin
import org.w3c.dom.events.Event
import kotlin.js.ExperimentalWasmJsInterop
import kotlin.js.js

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKoin()
    val lifecycle = LifecycleRegistry()
    val rootComponent = createRootComponent(
        componentContext = DefaultComponentContext(lifecycle = lifecycle)
    )
    lifecycle.attachToDocument()

    ComposeViewport {
        val density = LocalDensity.current
        val orientation = rememberOrientation()

        // Вычисляем минимум в пикселях
        val (minW, minH) = remember(orientation, density) {
            val w = when (orientation) {
                Orientation.Horizontal -> MIN_SCREEN_HORIZONTAL_WIDTH.dp
                Orientation.Vertical -> MIN_SCREEN_VERTICAL_WIDTH.dp
            }
            val h = when (orientation) {
                Orientation.Horizontal -> MIN_SCREEN_HORIZONTAL_HEIGHT.dp
                Orientation.Vertical -> MIN_SCREEN_VERTICAL_HEIGHT.dp
            }
            w to h
        }

        val verticalScrollState = rememberScrollState()
        val horizontalScrollState = rememberScrollState()

        // ✅ RootScreen остаётся ЧИСТЫМ
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val maxWidth = maxWidth
            val maxHeight = maxHeight

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(state = horizontalScrollState)
                    .verticalScroll(state = verticalScrollState)
            ) {
                RootScreen(
                    modifier = Modifier
                        .width(maxWidth.coerceAtLeast(minW))
                        .height(maxHeight.coerceAtLeast(minH)),
                    rootComponent = rootComponent,
                    orientation = orientation
                )
            }

            if (maxWidth < minW) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(PADDINGS.dp)
                        .background(color = Color.DarkGray)
                        .padding(end = if (maxHeight < minH) PADDINGS.dp else 0.dp)
                        .background(color = Color.Magenta, shape = RoundedCornerShape(PADDINGS.dp))
                ) {
                    // todo: add thumb later!
                }
            }
            if (maxHeight < minH) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .fillMaxHeight()
                        .width(PADDINGS.dp)
                        .background(color = Color.DarkGray)
                        .padding(bottom = if (maxWidth < minW) PADDINGS.dp else 0.dp)
                        .background(color = Color.Magenta, shape = RoundedCornerShape(PADDINGS.dp))
                ) {
                    // todo: add thumb later!
                }
            }
        }
    }
}

@OptIn(ExperimentalWasmJsInterop::class)
private fun getVisibilityState(): String = js("document.visibilityState")

private fun LifecycleRegistry.attachToDocument() {
    val callback: (Event) -> Unit = { _ ->
        if (getVisibilityState() == "visible") resume() else stop()
    }

    // Применяем состояние при инициализации
    if (getVisibilityState() == "visible") resume() else stop()

    document.addEventListener("visibilitychange", callback)
    doOnDestroy { document.removeEventListener("visibilitychange", callback) }
}
