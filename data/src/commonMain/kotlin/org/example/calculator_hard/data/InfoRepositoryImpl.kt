package org.example.calculator_hard.data

import org.example.calculator_hard.domain.Info
import org.example.calculator_hard.domain.InfoRepository
import org.koin.core.annotation.Single

@Single
internal class InfoRepositoryImpl : InfoRepository {
    override suspend fun getInfo() = Info(name = getPlatform().name)
}
