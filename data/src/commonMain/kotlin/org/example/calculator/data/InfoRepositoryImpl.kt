package org.example.calculator.data

import org.example.calculator.domain.Info
import org.example.calculator.domain.InfoRepository

internal class InfoRepositoryImpl : InfoRepository {
    override suspend fun getInfo() = Info(name = getPlatform().name)
}
