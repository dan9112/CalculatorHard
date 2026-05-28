package org.example.calculator_hard.data

import org.example.calculator_hard.domain.CalculationRepository
import org.koin.dsl.module

val webDataModule = module {
    single<CalculationRepository> { FakeCalculationRepository() }
}
