package org.example.calculator.data

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module

internal actual fun Module.platformInjections() {
    single { DriverFactory(context = androidContext()) }

    factory<ObservableSettings> { params ->
        SharedPreferencesSettings
            .Factory(androidContext())
            .create(params.get())
    }
}
