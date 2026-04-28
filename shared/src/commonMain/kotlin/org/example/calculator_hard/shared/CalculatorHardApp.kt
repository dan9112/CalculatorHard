package org.example.calculator_hard.shared

import org.example.calculator_hard.data.DataModule
import org.example.calculator_hard.presentation.PresentationModule
import org.koin.core.annotation.KoinApplication
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration
import org.koin.plugin.module.dsl.startKoin

@KoinApplication(modules = [DataModule::class, PresentationModule::class])
private class CalculatorHardApp

private fun privateStartKoin(appDeclaration: KoinAppDeclaration? = null) {
    startKoin<CalculatorHardApp> {
        printLogger(Level.DEBUG)
        appDeclaration?.invoke(this)
    }
}

fun startKoin(appDeclaration: KoinAppDeclaration) =
    privateStartKoin(appDeclaration = appDeclaration)

fun startKoin() = privateStartKoin(appDeclaration = null)
