package org.example.calculator_hard.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.calculator_hard.SQLDelightDatabase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.example.calculator_hard.domain.Calculation
import org.example.calculator_hard.domain.CalculationRepository
import org.example.calculator_hard.domain.Operation

class SqlDelightCalculationRepository(private val databaseDeferred: Deferred<SQLDelightDatabase>) :
    CalculationRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override val calculations: Flow<List<Calculation>> = channelFlow {
        // 1. Ждем базу данных ОДИН раз при старте подписки
        val db = databaseDeferred.await()

        // 2. Напрямую слушаем обновления SQLDelight
        db.calculationsQueries.getAllCalculations()
            .asFlow()
            .mapToList(context = Dispatchers.Default)
            .map { list ->
                list.map { entity ->
                    Calculation(
                        id = entity.id,
                        numbers = entity.numbers,
                        operations = entity.operations,
                        result = entity.result
                    )
                }
            }
            // 3. Отправляем все обновления в канал
            .collectLatest { transformedList ->
                send(transformedList)
            }
    }

    override suspend fun addCalculation(
        numbers: List<Float>,
        operations: List<Operation>,
        result: Double?
    ): Long = withContext(context = Dispatchers.Default) {
        val db = databaseDeferred.await()

        db
            .calculationsQueries
            .transactionWithResult {
                db
                    .calculationsQueries
                    .insertCalculation(
                        numbers = numbers,
                        operations = operations,
                        result = result
                    )
                db
                    .calculationsQueries
                    .lastInsertId()
                    .executeAsOne()
            }
    }
}
