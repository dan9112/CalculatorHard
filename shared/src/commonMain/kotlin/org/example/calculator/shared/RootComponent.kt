package org.example.calculator.shared

import com.arkivanov.decompose.ComponentContext
import org.example.calculator.domain.CalculationRepository
import org.example.calculator.presentation.createRootComponent
import org.koin.mp.KoinPlatform.getKoin

fun createRootComponent(componentContext: ComponentContext) = createRootComponent(
    componentContext = componentContext,
    calculationRepository = getKoin().get<CalculationRepository>(),
)
