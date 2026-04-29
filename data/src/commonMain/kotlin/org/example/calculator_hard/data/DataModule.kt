package org.example.calculator_hard.data

import org.example.calculator_hard.domain.InfoRepository
import org.koin.dsl.module

val dataModule = module {
    single<InfoRepository> { InfoRepositoryImpl() }
}
