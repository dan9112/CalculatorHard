package org.example.calculator_hard.shared

import android.content.Context
import org.example.calculator_hard.data.DriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun startKoin(context: Context) = startKoin {
    androidContext(androidContext = context)
}

internal actual val platformSqlDelightDependenciesModule = module {
    single { DriverFactory(context = androidContext()) }
}
