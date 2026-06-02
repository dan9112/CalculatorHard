package org.example.calculator.domain

import kotlinx.coroutines.flow.Flow

interface CalculationRepository {
    /** Реактивный поток конкретной страницы. Переэмитит при любом изменении БД */
    fun getPageFlow(
        page: Int,
        pageSize: Int,
    ): Flow<PageData<Calculation>>

    suspend fun addCalculation(
        numbers: List<Float>,
        operations: List<Operation>,
        result: Double?,
    ): Long

    suspend fun deleteCalculationById(id: Long)
}
