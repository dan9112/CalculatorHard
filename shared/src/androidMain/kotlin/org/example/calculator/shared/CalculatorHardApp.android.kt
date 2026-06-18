package org.example.calculator.shared

import android.content.Context
import org.koin.android.ext.koin.androidContext

fun startKoin(context: Context) = startKoin {
    androidContext(androidContext = context)
}
