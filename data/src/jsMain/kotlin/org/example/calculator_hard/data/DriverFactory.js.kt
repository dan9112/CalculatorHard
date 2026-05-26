package org.example.calculator_hard.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.worker.createDefaultWebWorkerDriver

actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver = createDefaultWebWorkerDriver()
}
