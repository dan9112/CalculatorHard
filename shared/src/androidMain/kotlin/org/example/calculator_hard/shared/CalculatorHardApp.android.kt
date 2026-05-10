package org.example.calculator_hard.shared

import android.content.Context
import org.koin.android.ext.koin.androidContext

fun startKoin(context: Context) = startKoin {
    androidContext(androidContext = context)
}
