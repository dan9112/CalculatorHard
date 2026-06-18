package org.example.calculator.data

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.parameter.parametersOf
import java.util.prefs.Preferences

internal actual fun Module.platformInjections() {
    factory<PreferencesSettings.Factory> { params ->
        PreferencesSettings.Factory(
            params
                .getOrNull()
                ?: Preferences.userRoot(),
        )
    }

    factory<ObservableSettings> { params ->
        // todo: add param later!
        get<PreferencesSettings.Factory> { parametersOf() }
            .create(params.get())
    }

    singleOf(constructor = ::DriverFactory)
}
