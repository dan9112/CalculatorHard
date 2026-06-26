package org.example.calculator

import android.app.Application
import io.kotzilla.generated.monitoring
import org.example.calculator.shared.startKoin
import org.koin.android.ext.koin.androidContext

class CalculatorApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(androidContext = this@CalculatorApplication)
            monitoring()
        }
//        startKoin(context = this)
    }
}
