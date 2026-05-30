package org.example.calculator_hard.shared

import org.example.calculator_hard.data.DriverFactory
import org.koin.dsl.module

internal actual val platformSqlDelightDependenciesModule = module {
    single { DriverFactory() }
}
