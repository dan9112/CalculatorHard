package org.example.calculator_hard.domain

import kotlinx.coroutines.flow.Flow

interface CalculationRepository {
    /**
     * Поток с пагинацией и сортировкой по убыванию ID (новые записи сверху).
     * @param limit количество записей на странице
     * @param offset смещение для пагинации
     */
    fun getCalculationsFlow(limit: Int = 20, offset: Int = 0): Flow<List<Calculation>>

    suspend fun addCalculation(
        numbers: List<Float>,
        operations: List<Operation>,
        result: Double?
    ): Long

    /**
     * Удаляет запись из истории по ID.
     */
    suspend fun deleteCalculationById(id: Long)
}
