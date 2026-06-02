package org.example.calculator.shared

import org.example.calculator.data.DriverFactory
import org.koin.dsl.module

internal actual val platformSqlDelightDependenciesModule =
    module {
        single { DriverFactory() }
    }
