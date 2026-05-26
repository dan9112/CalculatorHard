package org.example.calculator_hard.data

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.example.calculator_hard.SQLDelightDatabase

actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver = NativeSqliteDriver(
        schema = SQLDelightDatabase.Schema.synchronous(),
        name = "test.db"
    )
}
