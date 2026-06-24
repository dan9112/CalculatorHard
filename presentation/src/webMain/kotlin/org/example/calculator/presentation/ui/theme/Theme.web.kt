package org.example.calculator.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
actual fun dynamicColorScheme(darkTheme: Boolean): ColorScheme? = null

@Composable
actual fun isSystemDark() = isSystemInDarkTheme()
