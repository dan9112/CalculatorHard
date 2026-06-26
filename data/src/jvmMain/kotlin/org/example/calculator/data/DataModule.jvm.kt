package org.example.calculator.data

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import java.util.prefs.Preferences

actual val platformModule = module {
    single<PreferencesSettings.Factory> {
        PreferencesSettings.Factory(Preferences.userRoot())
    }

    single<ObservableSettings> {
        get<PreferencesSettings.Factory>().create("app_settings")
    }

    singleOf(constructor = ::DriverFactory)
    includes(defaultRepositoriesModule)
}
