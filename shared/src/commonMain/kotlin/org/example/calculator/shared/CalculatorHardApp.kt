package org.example.calculator.shared

import org.example.calculator.data.dataModule
import org.example.calculator.data.platformModule
import org.example.calculator.presentation.presentationModule
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration

private fun privateStartKoin(appDeclaration: KoinAppDeclaration? = null) {
    startKoin {
        appDeclaration?.invoke(this)
        printLogger(Level.DEBUG)
        modules(
            platformModule,
            dataModule,
            presentationModule,
        )
    }
}

fun startKoin(appDeclaration: KoinAppDeclaration) = privateStartKoin(appDeclaration = appDeclaration)
