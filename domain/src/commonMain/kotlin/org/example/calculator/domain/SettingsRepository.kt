package org.example.calculator.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val darkTheme: Flow<Boolean?>
    val themeContrastLevel: Flow<Boolean?>
    val themeDynamicColors: Flow<Boolean>?

    fun updateDarkTheme(newValue: Boolean?)
    fun updateThemeContrastLevel(newValue: Boolean?)
    fun updateThemeDynamicColors(newValue: Boolean)
}
