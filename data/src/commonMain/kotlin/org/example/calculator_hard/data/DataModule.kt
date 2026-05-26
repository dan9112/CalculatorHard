package org.example.calculator_hard.data

import com.example.calculator_hard.SQLDelightDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.example.calculator_hard.domain.CalculationRepository
import org.example.calculator_hard.domain.InfoRepository
import org.koin.dsl.module

val dataModule = module {
    single<InfoRepository> { InfoRepositoryImpl() }
    single<CalculationRepository> { CalculationRepositoryImpl(databaseDeferred = get()) }

    single<Deferred<SQLDelightDatabase>> {
        CoroutineScope(context = Dispatchers.Default).async {
            val factory: DriverFactory = get()
            createDatabase(factory)
        }
    }
}
