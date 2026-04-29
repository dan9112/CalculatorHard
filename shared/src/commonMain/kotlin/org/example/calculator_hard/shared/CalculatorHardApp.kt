package org.example.calculator_hard.shared

//import io.kotzilla.generated.monitoring
import org.example.calculator_hard.data.dataModule
import org.example.calculator_hard.presentation.presentationModule
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.dsl.KoinAppDeclaration

private fun privateStartKoin(appDeclaration: KoinAppDeclaration? = null) {
    startKoin {
        printLogger(Level.DEBUG)
        modules(
            dataModule,
            presentationModule
        )
        appDeclaration?.invoke(this)

//        monitoring()
    }
}

fun startKoin(appDeclaration: KoinAppDeclaration) =
    privateStartKoin(appDeclaration = appDeclaration)

fun startKoin() = privateStartKoin(appDeclaration = null)
