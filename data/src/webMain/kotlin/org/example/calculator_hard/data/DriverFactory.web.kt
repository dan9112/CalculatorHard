package org.example.calculator_hard.data

import app.cash.sqldelight.db.SqlDriver

actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver = TODO(
        reason = "Replace with the official SQLDelight Web implementation once it is supported/ready"
    )
}
