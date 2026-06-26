package org.example.calculator.data

import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.example.calculator.domain.CalculationRepository
import org.example.calculator.domain.SettingsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    single<Settings> { StorageSettings() }
    single<SettingsRepository> { MultiplatformSettingsRepositoryWeb(settings = get()) }
    single<CalculationRepository> { FakeCalculationRepository() }

    singleOf(constructor = ::DriverFactory)
}
