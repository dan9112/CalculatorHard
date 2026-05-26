package org.example.calculator_hard.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver

actual class DriverFactory {
    @OptIn(ExperimentalWasmJsInterop::class)
    actual suspend fun createDriver(): SqlDriver = createDefaultWebWorkerDriver()
}
