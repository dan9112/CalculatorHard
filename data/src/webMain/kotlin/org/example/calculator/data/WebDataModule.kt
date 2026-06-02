package org.example.calculator.data

import org.example.calculator.domain.CalculationRepository
import org.koin.dsl.module

val webDataModule =
    module {
        single<CalculationRepository> { FakeCalculationRepository() }
    }
