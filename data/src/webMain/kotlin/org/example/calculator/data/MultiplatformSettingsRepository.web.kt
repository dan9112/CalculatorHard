package org.example.calculator.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.calculator.domain.SettingsRepository

class MultiplatformSettingsRepositoryWeb(private val settings: Settings) : SettingsRepository {
    override val darkTheme: StateFlow<Boolean?>
        field = MutableStateFlow(value = settings.getBooleanOrNull(key = DARK_KEY))

    override val themeContrastLevel: StateFlow<Boolean?>
        field = MutableStateFlow(value = settings.getBooleanOrNull(key = CONTRAST_KEY))

    override val themeDynamicColors: StateFlow<Boolean>?
        field = if (isDynamicColorsSupport) {
            MutableStateFlow(value = settings.getBoolean(key = DYNAMIC_KEY, defaultValue = true))
        } else {
            null
        }

    override fun updateDarkTheme(newValue: Boolean?) {
        settings[DARK_KEY] = newValue
        darkTheme.value = newValue
    }

    override fun updateThemeContrastLevel(newValue: Boolean?) {
        settings[CONTRAST_KEY] = newValue
        themeContrastLevel.value = newValue
    }

    override fun updateThemeDynamicColors(newValue: Boolean) {
        themeDynamicColors?.run {
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
