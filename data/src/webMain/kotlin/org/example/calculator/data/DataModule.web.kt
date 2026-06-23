package org.example.calculator.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.Storage
import com.russhwolf.settings.StorageSettings
import org.example.calculator.domain.CalculationRepository
import org.example.calculator.domain.SettingsRepository
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf

internal actual fun Module.platformInjections() {
    factory<Settings> { params ->
        params
            .getOrNull<Storage>()
            ?.let(block = ::StorageSettings)
            ?: StorageSettings()
    }

    single<SettingsRepository> { MultiplatformSettingsRepositoryWeb(settings = get()) }
    single<CalculationRepository> { FakeCalculationRepository() }

    singleOf(constructor = ::DriverFactory)
}
