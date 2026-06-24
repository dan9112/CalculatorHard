package org.example.calculator

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val CalculatorIcon by lazy {
    ImageVector.Builder(
        name = "CalculatorIcon",
        defaultWidth = 108.dp,
        defaultHeight = 108.dp,
        viewportWidth = 1024f,
        viewportHeight = 1024f,
    ).apply {
        group(
            scaleX = 0.63461536f,
            scaleY = 0.63461536f,
            translationX = 187.07692f,
            translationY = 187.07692f,
        ) {
            path {
                moveTo(0f, 0f)
                horizontalLineToRelative(1024f)
                verticalLineToRelative(1024f)
                horizontalLineToRelative(-1024f)
                close()
            }
            path(
                fill = SolidColor(Color.White),
                fillAlpha = 0.98f,
                strokeAlpha = 0.98f,
            ) {
                moveTo(280f, 150f)
                lineTo(744f, 150f)
                arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 824f, 230f)
                lineTo(824f, 794f)
                arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 744f, 874f)
                lineTo(280f, 874f)
                arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 200f, 794f)
                lineTo(200f, 230f)
                arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 280f, 150f)
                close()
            }
            path(fill = SolidColor(Color(0xFFE8F4F8))) {
                moveTo(280f, 200f)
                lineTo(744f, 200f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 764f, 220f)
                lineTo(764f, 300f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 744f, 320f)
                lineTo(280f, 320f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 260f, 300f)
                lineTo(260f, 220f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 280f, 200f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(260f, 360f),
                    end = Offset(260f, 470f),
                ),
            ) {
                moveTo(280f, 360f)
                lineTo(350f, 360f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 370f, 380f)
                lineTo(370f, 450f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 350f, 470f)
                lineTo(280f, 470f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 260f, 450f)
                lineTo(260f, 380f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 280f, 360f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(395f, 360f),
                    end = Offset(395f, 470f),
                ),
            ) {
                moveTo(415f, 360f)
                lineTo(485f, 360f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 505f, 380f)
                lineTo(505f, 450f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 485f, 470f)
                lineTo(415f, 470f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 395f, 450f)
                lineTo(395f, 380f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 415f, 360f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(530f, 360f),
                    end = Offset(530f, 470f),
                ),
            ) {
                moveTo(550f, 360f)
                lineTo(620f, 360f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 640f, 380f)
                lineTo(640f, 450f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 620f, 470f)
                lineTo(550f, 470f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 530f, 450f)
                lineTo(530f, 380f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 550f, 360f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3C00E6))) {
                moveTo(685f, 360f)
                lineTo(755f, 360f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 775f, 380f)
                lineTo(775f, 450f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 755f, 470f)
                lineTo(685f, 470f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 665f, 450f)
                lineTo(665f, 380f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 685f, 360f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(260f, 490f),
                    end = Offset(260f, 600f),
                ),
            ) {
                moveTo(280f, 490f)
                lineTo(350f, 490f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 370f, 510f)
                lineTo(370f, 580f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 350f, 600f)
                lineTo(280f, 600f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 260f, 580f)
                lineTo(260f, 510f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 280f, 490f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(395f, 490f),
                    end = Offset(395f, 600f),
                ),
            ) {
                moveTo(415f, 490f)
                lineTo(485f, 490f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 505f, 510f)
                lineTo(505f, 580f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 485f, 600f)
                lineTo(415f, 600f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 395f, 580f)
                lineTo(395f, 510f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 415f, 490f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(530f, 490f),
                    end = Offset(530f, 600f),
                ),
            ) {
                moveTo(550f, 490f)
                lineTo(620f, 490f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 640f, 510f)
                lineTo(640f, 580f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 620f, 600f)
                lineTo(550f, 600f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 530f, 580f)
                lineTo(530f, 510f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 550f, 490f)
                close()
            }
            path(fill = SolidColor(Color(0xFF3C00E6))) {
                moveTo(685f, 490f)
                lineTo(755f, 490f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 775f, 510f)
                lineTo(775f, 580f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 755f, 600f)
                lineTo(685f, 600f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 665f, 580f)
                lineTo(665f, 510f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 685f, 490f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(260f, 620f),
                    end = Offset(260f, 730f),
                ),
            ) {
                moveTo(280f, 620f)
                lineTo(350f, 620f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 370f, 640f)
                lineTo(370f, 710f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 350f, 730f)
                lineTo(280f, 730f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 260f, 710f)
                lineTo(260f, 640f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 280f, 620f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(395f, 620f),
                    end = Offset(395f, 730f),
                ),
            ) {
                moveTo(415f, 620f)
                lineTo(485f, 620f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 505f, 640f)
                lineTo(505f, 710f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 485f, 730f)
                lineTo(415f, 730f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 395f, 710f)
                lineTo(395f, 640f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 415f, 620f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(530f, 620f),
                    end = Offset(530f, 730f),
                ),
            ) {
                moveTo(550f, 620f)
                lineTo(620f, 620f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 640f, 640f)
                lineTo(640f, 710f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 620f, 730f)
                lineTo(550f, 730f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 530f, 710f)
                lineTo(530f, 640f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 550f, 620f)
                close()
            }
            path(fill = SolidColor(Color(0xFF8401E5))) {
                moveTo(685f, 620f)
                lineTo(755f, 620f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 775f, 640f)
                lineTo(775f, 710f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 755f, 730f)
                lineTo(685f, 730f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 665f, 710f)
                lineTo(665f, 640f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 685f, 620f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(260f, 750f),
                    end = Offset(260f, 860f),
                ),
            ) {
                moveTo(280f, 750f)
                lineTo(485f, 750f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 505f, 770f)
                lineTo(505f, 840f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 485f, 860f)
                lineTo(280f, 860f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 260f, 840f)
                lineTo(260f, 770f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 280f, 750f)
                close()
            }
            path(
                fill = Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to Color.White,
                        1f to Color(0xFFF0F4F8),
                    ),
                    start = Offset(530f, 750f),
                    end = Offset(530f, 860f),
                ),
            ) {
                moveTo(550f, 750f)
                lineTo(620f, 750f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 640f, 770f)
                lineTo(640f, 840f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 620f, 860f)
                lineTo(550f, 860f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 530f, 840f)
                lineTo(530f, 770f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 550f, 750f)
                close()
            }
            path(fill = SolidColor(Color(0xFF8401E5))) {
                moveTo(685f, 750f)
                lineTo(755f, 750f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 775f, 770f)
                lineTo(775f, 840f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 755f, 860f)
                lineTo(685f, 860f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 665f, 840f)
                lineTo(665f, 770f)
                arcTo(20f, 20f, 0f, isMoreThanHalf = false, isPositiveArc = true, 685f, 750f)
                close()
            }
        }
    }.build()
}

@Preview
@Composable
private fun Preview() {
    Image(
        imageVector = CalculatorIcon,
        contentDescription = null,
        modifier = Modifier.size(400.dp, 400.dp),
        contentScale = ContentScale.Crop,
    )
}
