package org.example.calculator.presentation

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalWindowInfo
import org.example.calculator.presentation.ContextItemScope.LabelData.ResourceData
import org.example.calculator.presentation.ContextItemScope.LabelData.StringData
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

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

internal actual fun ContextItemScope.copyToClipboard(text: String) {
    val selection = StringSelection(text)
    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
    clipboard.setContents(selection, null)
}

@Composable
internal actual fun AppContextMenu(
    menuContent: (ContextItemScope.() -> Unit),
    content: @Composable (() -> Unit),
) {
    val scope = remember { ContextItemScope().apply { menuContent() } }

    val items = scope
        .items
        .map {
            when (val label = it.first) {
                is ResourceData -> stringResource(label.value)
                is StringData -> label.value
            } to it.second
        }

    ContextMenuArea(
        items = {
            items.map { (label, onClick) ->
                ContextMenuItem(label, onClick)
            }
        },
        content = content,
    )
}
