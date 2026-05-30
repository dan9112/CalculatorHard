package org.example.calculator_hard.shared

import com.arkivanov.decompose.ComponentContext
import org.example.calculator_hard.domain.CalculationRepository
import org.example.calculator_hard.presentation.createRootComponent
import org.koin.mp.KoinPlatform.getKoin

fun createRootComponent(componentContext: ComponentContext) = createRootComponent(
    componentContext = componentContext,
    calculationRepository = getKoin().get<CalculationRepository>()
)
