package org.example.calculator.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.Wallpapers
import androidx.compose.ui.unit.dp
import org.example.calculator.presentation.ui.theme.AppTheme
import org.example.calculator.presentation.ui.theme.ContrastLevel

actual val dynamicColorsSupport = true

@Preview(wallpaper = Wallpapers.GREEN_DOMINATED_EXAMPLE)
@Composable
private fun Preview() {
    var theme by rememberSaveable { mutableStateOf<Boolean?>(value = null) }
    var contrast by rememberSaveable(
        stateSaver = Saver(
            save = { it.ordinal },
            restore = { ContrastLevel.entries[it] },
        ),
    ) { mutableStateOf(value = ContrastLevel.Normal) }
    var dynamic by rememberSaveable { mutableStateOf(value = true) }

    AppTheme(
        darkTheme = theme ?: isSystemInDarkTheme(),
        contrastLevel = contrast,
        dynamicColor = dynamic,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(all = 8.dp),
        ) {
            ExpandableSettingsPanel(
                modifier = Modifier.align(Alignment.TopStart),
                theme = theme to { theme = it },
                contrast = contrast to { contrast = it },
                dynamic = dynamic to { dynamic = it },
            )

            Column(
                modifier = Modifier.align(Alignment.BottomEnd),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("Dynamic: ")
                        }
                        withStyle(SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                            append(
                                when (dynamic) {
                                    true -> "yes"
                                    false -> "no"
                                },
                            )
                        }
                    },
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                            append("Contrast: ")
                        }
                        withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                            append(
                                when (contrast) {
                                    ContrastLevel.High -> "max"
                                    ContrastLevel.Medium -> "high"
                                    ContrastLevel.Normal -> "default"
                                },
                            )
                        }
                    },
                )
            }
        }
    }
}
