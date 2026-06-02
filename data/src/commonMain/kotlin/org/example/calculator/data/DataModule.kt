package org.example.calculator.data

import com.example.calculator.SQLDelightDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.example.calculator.domain.CalculationRepository
import org.example.calculator.domain.InfoRepository
import org.koin.dsl.module

val dataModule =
    module {
        single<InfoRepository> { InfoRepositoryImpl() }
        if (!getPlatform().name.startsWith(prefix = "Web")) {
            single<CalculationRepository> { SqlDelightCalculationRepository(databaseDeferred = get()) }

            single<Deferred<SQLDelightDatabase>> {
                CoroutineScope(context = Dispatchers.Default).async {
                    val factory: DriverFactory = get()
                    createDatabase(factory)
                }
            }
        }
    }
