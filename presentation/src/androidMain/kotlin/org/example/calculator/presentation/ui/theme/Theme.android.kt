package org.example.calculator.presentation.ui.theme

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.S
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun dynamicColorScheme(darkTheme: Boolean) = if (SDK_INT >= S) {
    val localContext = LocalContext.current
    if (darkTheme) dynamicDarkColorScheme(localContext) else dynamicLightColorScheme(localContext)
} else {
    null
}
