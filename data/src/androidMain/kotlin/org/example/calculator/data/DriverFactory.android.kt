package org.example.calculator.data

import android.content.Context
import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.example.calculator.SQLDelightDatabase

actual class DriverFactory(
    private val context: Context,
) {
    actual suspend fun createDriver(): SqlDriver = AndroidSqliteDriver(
        schema = SQLDelightDatabase.Schema.synchronous(),
        context = context,
        name = "test.db",
    )
}
