package org.example.calculator

import android.app.Application
import org.example.calculator.shared.startKoin

class CalculatorApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin(context = this)
    }
}
