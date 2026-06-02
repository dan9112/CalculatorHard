package org.example.calculator_hard.data

import org.example.calculator_hard.domain.Info
import org.example.calculator_hard.domain.InfoRepository

internal class InfoRepositoryImpl : InfoRepository {
    override suspend fun getInfo() = Info(name = getPlatform().name)
}
