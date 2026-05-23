package org.example.calculator_hard.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import calculatorhard.presentation.generated.resources.Res
import calculatorhard.presentation.generated.resources.space_grotesk_bold
import calculatorhard.presentation.generated.resources.space_grotesk_light
import calculatorhard.presentation.generated.resources.space_grotesk_medium
import calculatorhard.presentation.generated.resources.space_grotesk_regular
import calculatorhard.presentation.generated.resources.space_grotesk_semi_bold
import org.jetbrains.compose.resources.Font

@Composable
internal fun bodyFontFamily() = FontFamily(
    Font(resource = Res.font.space_grotesk_bold, weight = FontWeight.Bold),
    Font(resource = Res.font.space_grotesk_light, weight = FontWeight.Light),
    Font(resource = Res.font.space_grotesk_medium, weight = FontWeight.Medium),
    Font(resource = Res.font.space_grotesk_regular, weight = FontWeight.Normal),
    Font(resource = Res.font.space_grotesk_semi_bold, weight = FontWeight.SemiBold)
)

@Composable
internal fun displayFontFamily() = bodyFontFamily()

@Composable
fun appTypography(
    bodyFontFamily: FontFamily = bodyFontFamily(),
    displayFontFamily: FontFamily = displayFontFamily()
) = Typography().let { baseline ->
    Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = displayFontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = displayFontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = displayFontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = displayFontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = displayFontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = displayFontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = displayFontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = displayFontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = displayFontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = bodyFontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = bodyFontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = bodyFontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = bodyFontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = bodyFontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = bodyFontFamily)
    )
}
