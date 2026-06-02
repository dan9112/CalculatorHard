package org.example.calculator.data

import app.cash.sqldelight.db.SqlDriver
import com.example.calculator.Calculations
import com.example.calculator.SQLDelightDatabase

expect class DriverFactory {
    suspend fun createDriver(): SqlDriver
}

suspend fun createDatabase(factory: DriverFactory): SQLDelightDatabase {
    val driver = factory.createDriver()
    SQLDelightDatabase.Schema.create(driver).await()
    return SQLDelightDatabase(
        driver = driver,
        CalculationsAdapter =
        Calculations.Adapter(
            numbersAdapter = FloatListAdapter,
            operationsAdapter = OperationListAdapter,
        ),
    )
}
