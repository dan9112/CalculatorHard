package org.example.calculator.presentation

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpOffset
import org.example.calculator.presentation.ContextItemScope.LabelData.ResourceData
import org.example.calculator.presentation.ContextItemScope.LabelData.StringData
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun rememberOrientation(): Orientation {
    val size = LocalWindowInfo.current.containerSize
    return if (size.width > size.height) Orientation.Horizontal else Orientation.Vertical
}

internal actual class ContextItemScope {
    val items = mutableListOf<Pair<LabelData, () -> Unit>>()

    sealed interface LabelData {
        data class StringData(val value: String) : LabelData
        data class ResourceData(val value: StringResource) : LabelData
    }

    actual fun item(label: String, onClick: () -> Unit) {
        items.add(StringData(label) to onClick)
    }

    actual fun item(label: StringResource, onClick: () -> Unit) {
        items.add(ResourceData(label) to onClick)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun AppContextMenu(
    menuContent: (ContextItemScope.() -> Unit),
    content: @Composable (() -> Unit),
) {
    val density = LocalDensity.current

    val scope = remember { ContextItemScope().apply { menuContent() } }
    var menuOffset by remember { mutableStateOf<Offset?>(value = null) }
    var lastDpOffset by remember { mutableStateOf(DpOffset.Zero) }
    LaunchedEffect(menuOffset) {
        menuOffset?.let {
            lastDpOffset = density.run { DpOffset(x = it.x.toDp(), y = it.y.toDp()) }
        }
    }

    Box(
        modifier = Modifier.pointerInput(menuOffset) {
            if (menuOffset == null) {
                awaitPointerEventScope {
                    val event = awaitPointerEvent()
                    if (event.button == PointerButton.Secondary) {
                        event.changes.forEach { it.consume() }
                        menuOffset = event.changes.firstOrNull()?.position
                    }
                }
            }
        },
    ) {
        content()

        DropdownMenu(
            expanded = menuOffset != null,
            onDismissRequest = { menuOffset = null },
            offset = lastDpOffset,
        ) {
            scope
                .items
                .forEach { (label, onClick) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = when (label) {
                                    is ResourceData -> stringResource(label.value)
                                    is StringData -> label.value
                                },
                            )
                        },
                        onClick = {
                            onClick()
                            menuOffset = null
                        },
                    )
                }
        }
    }
}
