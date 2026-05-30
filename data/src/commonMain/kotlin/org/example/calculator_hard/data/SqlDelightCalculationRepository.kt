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

class SqlDelightCalculationRepository(
    private val databaseDeferred: Deferred<SQLDelightDatabase>
) : CalculationRepository {

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCalculationsFlow(limit: Int, offset: Int): Flow<List<Calculation>> =
        channelFlow {
            // 1. Ждем инициализацию БД
        val db = databaseDeferred.await()

            // 2. Подписываемся на paginated query
            db.calculationsQueries.getCalculations(limit.toLong(), offset.toLong())
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
                // 3. Эмитим обновления в поток
                .collectLatest { send(it) }
    }

    override suspend fun addCalculation(
        numbers: List<Float>,
        operations: List<Operation>,
        result: Double?
    ): Long = withContext(Dispatchers.Default) {
        val db = databaseDeferred.await()
        val queries = db.calculationsQueries

        queries.transactionWithResult {
            queries.insertCalculation(
                numbers = numbers,
                operations = operations,
                result = result
            )
            queries.lastInsertId().executeAsOne()
        }
    }

    override suspend fun deleteCalculationById(id: Long): Unit = withContext(Dispatchers.Default) {
        val db = databaseDeferred.await()
        db.calculationsQueries.deleteCalculationById(id)
    }
}
