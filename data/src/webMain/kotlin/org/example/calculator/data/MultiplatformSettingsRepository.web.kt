package org.example.calculator.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.calculator.domain.SettingsRepository

class MultiplatformSettingsRepositoryWeb(private val settings: Settings) : SettingsRepository {
    private val _darkTheme = MutableStateFlow(value = settings.getBooleanOrNull(key = DARK_KEY))
    override val darkTheme = _darkTheme.asStateFlow()

    private val _themeContrastLevel = MutableStateFlow(value = settings.getBooleanOrNull(key = CONTRAST_KEY))
    override val themeContrastLevel = _themeContrastLevel.asStateFlow()

    private val _themeDynamicColors = if (isDynamicColorsSupport) {
        MutableStateFlow(value = settings.getBoolean(key = DYNAMIC_KEY, defaultValue = true))
    } else {
        null
    }
    override val themeDynamicColors = _themeDynamicColors?.asStateFlow()

    override fun updateDarkTheme(newValue: Boolean?) {
        settings[DARK_KEY] = newValue
        _darkTheme.value = newValue
    }

    override fun updateThemeContrastLevel(newValue: Boolean?) {
        settings[CONTRAST_KEY] = newValue
        _themeContrastLevel.value = newValue
    }

    override fun updateThemeDynamicColors(newValue: Boolean) {
        _themeDynamicColors?.run {
            settings[DYNAMIC_KEY] = newValue
            value = newValue
        }
    }

    private companion object {
        const val DARK_KEY = "dark"
        const val CONTRAST_KEY = "contrast"
        const val DYNAMIC_KEY = "dynamic"
    }
}

internal actual val isDynamicColorsSupport = false
