package org.example.calculator

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(onFinish: () -> Unit, modifier: Modifier = Modifier) {
    var targetAlpha by remember { mutableFloatStateOf(value = 0f) }
    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        label = "splash_alpha",
        animationSpec = tween(durationMillis = 3_000),
    )

    val finishTrigger by rememberUpdatedState(newValue = onFinish)

    LaunchedEffect(Unit) {
        // todo: move to component!
        targetAlpha = 1f
        delay(duration = 3.2.seconds)
        finishTrigger()
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Image(
            imageVector = CalculatorIcon,
            contentDescription = null,
            modifier = Modifier.size(256.dp),
            alpha = alpha,
        )
    }
}
