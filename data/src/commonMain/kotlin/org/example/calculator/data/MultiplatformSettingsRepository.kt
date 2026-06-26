package org.example.calculator.data

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getBooleanFlow
import com.russhwolf.settings.coroutines.getBooleanOrNullFlow
import com.russhwolf.settings.set
import org.example.calculator.domain.SettingsRepository

@OptIn(ExperimentalSettingsApi::class)
class MultiplatformSettingsRepository(private val observableSettings: ObservableSettings) : SettingsRepository {
    override val darkTheme by lazy {
        observableSettings.getBooleanOrNullFlow(key = DARK_KEY)
    }
    override val themeContrastLevel by lazy {
        observableSettings.getBooleanOrNullFlow(key = CONTRAST_KEY)
    }
    override val themeDynamicColors by lazy {
        if (isDynamicColorsSupport) {
            observableSettings.getBooleanFlow(key = DYNAMIC_KEY, defaultValue = true)
        } else {
            null
        }
    }

    override fun updateDarkTheme(newValue: Boolean?) {
        observableSettings[DARK_KEY] = newValue
    }

    override fun updateThemeContrastLevel(newValue: Boolean?) {
        observableSettings[CONTRAST_KEY] = newValue
    }

    override fun updateThemeDynamicColors(newValue: Boolean) {
        if (isDynamicColorsSupport) observableSettings[DYNAMIC_KEY] = newValue
    }

    private companion object {
        const val DARK_KEY = "dark"
        const val CONTRAST_KEY = "contrast"
        const val DYNAMIC_KEY = "dynamic"
    }
}

internal expect val isDynamicColorsSupport: Boolean
