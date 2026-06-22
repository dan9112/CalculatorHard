package org.example.calculator.presentation

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.util.Log.ASSERT
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpOffset
import org.example.calculator.presentation.ContextItemScope.LabelData.ResourceData
import org.example.calculator.presentation.ContextItemScope.LabelData.StringData
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@Composable
actual fun rememberOrientation(): Orientation {
    val configuration = LocalConfiguration.current
    return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Orientation.Horizontal
    } else {
        Orientation.Vertical
    }
}

internal actual class ContextItemScope(val context: Context) {
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

internal actual fun ContextItemScope.copyToClipboard(text: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    val clip = ClipData.newPlainText("Copied Text", text)
    clipboard.setPrimaryClip(clip)
}

@Composable
internal actual fun AppContextMenu(
    menuContent: (ContextItemScope.() -> Unit),
    content: @Composable (() -> Unit),
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = remember(context) {
        ContextItemScope(context).apply { menuContent() }
    }

    var menuOffset by remember { mutableStateOf<Offset?>(value = null) }
    var lastDpOffset by remember { mutableStateOf(DpOffset.Zero) }
    LaunchedEffect(menuOffset) {
        menuOffset?.let {
            lastDpOffset = density.run { DpOffset(x = it.x.toDp(), y = it.y.toDp()) }
        }
    }

    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .pointerInput(menuOffset) {
                if (menuOffset == null) {
                    detectTapGestures(
                        onLongPress = { offset ->
                            Log.println(
                                ASSERT,
                                "ContextMenu",
                                scope.items.joinToString(separator = "\n"),
                            )
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            menuOffset = offset
                        },
                    )
                }
            }
            .pointerInput(menuOffset) {
                if (menuOffset == null) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()

                            if (event.type == PointerEventType.Press) {
                                if (event.buttons.isSecondaryPressed) {
                                    menuOffset = event
                                        .changes
                                        .firstOrNull()
                                        ?.position
                                    event
                                        .changes
                                        .forEach { it.consume() }
                                }
                            }
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
