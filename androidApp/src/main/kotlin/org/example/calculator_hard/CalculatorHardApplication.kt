package org.example.calculator_hard

import android.app.Application
import org.example.calculator_hard.shared.startKoin

class CalculatorHardApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin(context = this)
    }
}
