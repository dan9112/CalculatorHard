package org.example.calculator.data

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single { DriverFactory(context = androidContext()) }

    factory<ObservableSettings> { params ->
        SharedPreferencesSettings
            .Factory(androidContext())
            .create("app_settings")
    }
    includes(defaultRepositoriesModule)
}
