package org.example.calculator_hard.presentation.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import calculatorhard.presentation.generated.resources.*
import org.example.calculator_hard.presentation.ui.theme.AppTheme
import org.example.calculator_hard.presentation.ui.theme.ContrastLevel
import org.jetbrains.compose.resources.stringResource

@Composable
fun ExpandableSettingsPanel(
    modifier: Modifier = Modifier,
    theme: Pair<Boolean?, (Boolean?) -> Unit>,
    buttonShape: Shape = CircleShape,
    contrast: Pair<ContrastLevel, (ContrastLevel) -> Unit>,
    dynamic: Pair<Boolean, (Boolean) -> Unit>? = null
) {
    var isMenuOpen by remember { mutableStateOf(value = false) }

    // Состояния настроек
    val themeState = theme.first   // null: Системная, false: Светлая, true: Тёмная
    val contrastState = contrast.first  // null: Нормальная, false: Высокая, true: Максимальная

    // Динамический выбор иконок и текстов описания в зависимости от состояния
    val themeIcon = when (themeState) {
        null -> Icons.Filled.SystemSecurityUpdateGood
        false -> Icons.Filled.WbSunny
        true -> Icons.Filled.NightsStay
    }
    val themeDescription = when (themeState) {
        null -> stringResource(resource = Res.string.theme_system)
        false -> stringResource(resource = Res.string.theme_light)
        true -> stringResource(resource = Res.string.theme_dark)
    }

    val contrastIcon = when (contrastState) {
        ContrastLevel.Normal -> Icons.Outlined.Circle
        ContrastLevel.Medium -> Icons.Filled.Contrast // Полу-закрашенное солнце (символ изменения градации)
        ContrastLevel.High -> Icons.Filled.Circle      // Полностью залитый круг (символ максимального контраста)
    }
    val contrastDescription = when (contrastState) {
        ContrastLevel.Normal -> stringResource(resource = Res.string.contrast_normal)
        ContrastLevel.Medium -> stringResource(resource = Res.string.contrast_high)
        ContrastLevel.High -> stringResource(resource = Res.string.contrast_max)
    }

    val menuDescription = if (isMenuOpen) {
        stringResource(resource = Res.string.menu_button_close)
    } else {
        stringResource(resource = Res.string.menu_button_open)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
//            .background(MaterialTheme.colorScheme.surfaceVariant, shape)
//            .padding(4.dp)
    ) {
        // Анимированная кнопка-бургер/стрелка
        AnimatedMenuArrowIcon(
            isMenuState = !isMenuOpen,
            shape = buttonShape,
            contentDescription = menuDescription,
            onClick = { isMenuOpen = !isMenuOpen }
        )

        // Панель с кнопками настроек
        AnimatedVisibility(
            visible = isMenuOpen,
            enter = expandHorizontally(animationSpec = tween(durationMillis = 300)) + fadeIn(
                animationSpec = tween(durationMillis = 300)
            ),
            exit = shrinkHorizontally(animationSpec = tween(delayMillis = 300)) + fadeOut(
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                // Кнопка Темы
                SettingIconButton(
                    icon = themeIcon,
                    shape = buttonShape,
                    contentDescription = themeDescription,
                    onClick = {
                        theme
                            .second
                            .invoke(
                                when (themeState) {
                                    null -> false
                                    false -> true
                                    true -> null
                                }
                            )
                    }
                )

                // Кнопка Адаптивных цветов
                dynamic?.let { (isDynamicColor, invoke) ->
                    val dynamicColorIcon =
                        if (isDynamicColor) Icons.Filled.Wallpaper else Icons.Filled.Palette
                    val dynamicColorDescription = if (isDynamicColor) {
                        stringResource(resource = Res.string.dynamic_color_on)
                    } else {
                        stringResource(resource = Res.string.dynamic_color_off)
                    }

                    SettingIconButton(
                        icon = dynamicColorIcon,
                        shape = buttonShape,
                        contentDescription = dynamicColorDescription,
                        onClick = { invoke(!isDynamicColor) }
                    )
                }

                // Кнопка Контрастности
                SettingIconButton(
                    icon = contrastIcon,
                    shape = buttonShape,
                    enabled = dynamic?.first != true,
                    contentDescription = contrastDescription,
                    onClick = {
                        contrast
                            .second
                            .invoke(
                                when (contrastState) {
                                    ContrastLevel.Normal -> ContrastLevel.Medium
                                    ContrastLevel.Medium -> ContrastLevel.High
                                    ContrastLevel.High -> ContrastLevel.Normal
                                }
                            )
                    }
                )
            }
        }
    }
}

expect val dynamicColorsSupport: Boolean

@Composable
private fun SettingIconButton(
    icon: ImageVector,
    shape: Shape,
    enabled: Boolean = true,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(shape = shape)
            .background(color = MaterialTheme.colorScheme.secondary.copy(alpha = if (enabled) 1f else 0.6f))
            .clickable(enabled = enabled, role = Role.Button) { onClick() }
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null, // Текст уже привязан к Box через semantics для корректного фокуса
            tint = MaterialTheme.colorScheme.onSecondary.copy(alpha = if (enabled) 1f else 0.6f),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun AnimatedMenuArrowIcon(
    modifier: Modifier = Modifier,
    shape: Shape,
    isMenuState: Boolean,
    contentDescription: String,
    onClick: () -> Unit
) {
    val iconColor = MaterialTheme.colorScheme.onPrimary
    // 1. Прогресс морфинга крыльев (0f - бургер, 1f - стрелка)
    val morphProgress by animateFloatAsState(
        targetValue = if (isMenuState) 0f else 1f,
        animationSpec = tween(durationMillis = 300)
    )

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    // 2. Угол поворота самого компонента Box (на 90 градусов)
    val targetRotation = when {
        isMenuState -> 0f
        isRtl -> 90f   // Для RtL крутим по часовой
        else -> -90f  // Для LtR крутим против часовой
    }

    val rotationAngle by animateFloatAsState(
        targetValue = targetRotation,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = modifier
            .size(45.dp)
            .clip(shape)
            .background(color = MaterialTheme.colorScheme.primary)
            .rotate(rotationAngle) // Поворот всей кнопки
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .semantics { this.contentDescription = contentDescription }
            .drawWithCache {
                onDrawWithContent {
                    val width = size.width
                    val height = size.height
                    val strokeWidth = 2.5.dp.toPx()

                    // Границы иконки
                    val leftX = width * 0.30f
                    val rightX = width * 0.70f
                    val centerX = width * 0.5f
                    val centerY = height * 0.5f

                    val topY = height * 0.32f
                    val midY = height * 0.50f
                    val bottomY = height * 0.68f

                    // Конечные координаты крыльев стрелки (пока она смотрит ВВЕРХ на холсте)
                    val arrowTipY = height * 0.30f
                    val arrowWingBaseY = height * 0.46f

                    // 1. ВЕРХНЯЯ ЛИНИЯ: Морфирует в левое крыло стрелки вверх
                    val topStart = Offset(
                        x = lerp(leftX, centerX - (width * 0.15f), morphProgress),
                        y = lerp(topY, arrowWingBaseY, morphProgress)
                    )
                    val topEnd = Offset(
                        x = lerp(rightX, centerX, morphProgress),
                        y = lerp(topY, arrowTipY, morphProgress)
                    )

                    // 3. НИЖНЯЯ ЛИНИЯ: Морфирует в правое крыло стрелки вверх
                    val bottomStart = Offset(
                        x = lerp(leftX, centerX + (width * 0.15f), morphProgress),
                        y = lerp(bottomY, arrowWingBaseY, morphProgress)
                    )
                    val bottomEnd = Offset(
                        x = lerp(rightX, centerX, morphProgress),
                        y = lerp(bottomY, arrowTipY, morphProgress)
                    )

                    // Рисуем верхнюю и нижнюю линии (они вращаются вместе с Box)
                    drawLine(
                        color = iconColor,
                        start = topStart,
                        end = topEnd,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = iconColor,
                        start = bottomStart,
                        end = bottomEnd,
                        strokeWidth = strokeWidth,
                        cap = StrokeCap.Round
                    )


                    // == МАГИЯ КОМПЕНСАЦИИ ДЛЯ СРЕДНЕЙ ЛИНИИ ==
                    // Мы вращаем холст для средней линии в ПРОТИВОПОЛОЖНУЮ сторону на точно такой же угол.
                    // Если Box повернулся на -90°, мы повернем линию внутри на +90°.
                    // В итоге для глаз пользователя линия останется стоять на месте горизонтально!
                    withTransform(
                        transformBlock = {
                            rotate(
                                degrees = -rotationAngle, // Минус компенсирует вращение Box
                                pivot = Offset(centerX, centerY)
                            )
                        }
                    ) {
                        // Сама средняя линия рисуется как статичная горизонтальная полоса бургера.
                        // Под действием противохода вращения она будет плавно сдвигать свои оси,
                        // оставаясь горизонтальной в мировых координатах экрана.
                        drawLine(
                            color = iconColor,
                            start = Offset(leftX, midY),
                            end = Offset(rightX, midY),
                            strokeWidth = strokeWidth,
                            cap = StrokeCap.Round
                        )
                    }
                }
            }
    )
}


@Preview
@Composable
private fun Preview() {
    var theme by rememberSaveable { mutableStateOf<Boolean?>(value = null) }
    var contrast by rememberSaveable(
        stateSaver = Saver(
            save = { it.ordinal },
            restore = { ContrastLevel.entries[it] }
        )
    ) { mutableStateOf(value = ContrastLevel.Normal) }

    // todo: replace with custom theme!
    AppTheme(
        darkTheme = theme ?: isSystemInDarkTheme(),
        contrastLevel = contrast
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background)
                .padding(all = 8.dp)
        ) {
            ExpandableSettingsPanel(
                modifier = Modifier.align(Alignment.TopStart),
                theme = theme to { theme = it },
                contrast = contrast to { contrast = it }
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append("Contrast: ")
                    }
                    withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.secondary)) {
                        append(
                            when (contrast) {
                                ContrastLevel.High -> "max"
                                ContrastLevel.Medium -> "high"
                                ContrastLevel.Normal -> "default"
                            }
                        )
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}
