package org.example.calculator.data

import app.cash.sqldelight.async.coroutines.synchronous
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.example.calculator.SQLDelightDatabase
import java.util.Properties

actual class DriverFactory {
    actual suspend fun createDriver(): SqlDriver {
        val driver: SqlDriver =
            JdbcSqliteDriver(
                url = "jdbc:sqlite:test.db",
                properties = Properties(),
                schema = SQLDelightDatabase.Schema.synchronous(),
            )
        return driver
    }
}
