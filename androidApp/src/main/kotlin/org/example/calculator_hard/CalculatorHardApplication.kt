package org.example.calculator_hard

import android.app.Application
import org.example.calculator_hard.shared.startKoin
import org.koin.android.ext.koin.androidContext

class CalculatorHardApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CalculatorHardApplication)
        }
    }
}
