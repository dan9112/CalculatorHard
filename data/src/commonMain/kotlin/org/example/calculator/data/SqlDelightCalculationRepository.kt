package org.example.calculator.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.example.calculator.SQLDelightDatabase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.example.calculator.domain.Calculation
import org.example.calculator.domain.CalculationRepository
import org.example.calculator.domain.Operation
import org.example.calculator.domain.PageData

class SqlDelightCalculationRepository(
    private val databaseDeferred: Deferred<SQLDelightDatabase>,
) : CalculationRepository {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPageFlow(
        page: Int,
        pageSize: Int,
    ) = channelFlow {
        databaseDeferred
            .await()
            .calculationsQueries
            .getCalculationsPage(
                limit =
                pageSize
                    .toLong()
                    .inc(),
                offset = (page * pageSize).toLong(),
            ).asFlow()
            .mapToList(context = Dispatchers.Default)
            .map {
                val hasNext = it.size > pageSize
                val list = if (hasNext) it.dropLast(1) else it
                PageData(
                    items =
                    list
                        .map { entity ->
                            Calculation(
                                id = entity.id,
                                numbers = entity.numbers,
                                operations = entity.operations,
                                result = entity.result,
                            )
                        },
                    hasNext = hasNext,
                )
            }.collectLatest { send(it) }
    }

    override suspend fun addCalculation(
        numbers: List<Float>,
        operations: List<Operation>,
        result: Double?,
    ) = withContext(Dispatchers.Default) {
        val calculationsQueries =
            databaseDeferred
                .await()
                .calculationsQueries
        calculationsQueries.transactionWithResult {
            calculationsQueries.insertCalculation(numbers, operations, result)
            calculationsQueries.lastInsertId().executeAsOne()
        }
    }

    override suspend fun deleteCalculationById(id: Long): Unit = withContext(Dispatchers.Default) {
        databaseDeferred.await().calculationsQueries.deleteCalculationById(id)
    }
}
