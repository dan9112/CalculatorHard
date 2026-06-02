package org.example.calculator_hard.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import calculatorhard.presentation.generated.resources.Res
import calculatorhard.presentation.generated.resources.compose_multiplatform
import kotlinx.coroutines.delay
import org.example.calculator_hard.domain.Info
import org.example.calculator_hard.domain.InfoRepository
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject

@Composable
//@Preview
fun App(greeting: Greeting = koinInject()) {
    MaterialTheme {
        var showContent by rememberSaveable { mutableStateOf(value = false) }
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(visible = showContent) {
                val info by produceState(initialValue = "Loading...") {
                    value = greeting.greet()
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $info")
                }
            }
        }
    }
}

@Preview
@Composable
private fun AppPreview() {
    App(
        greeting = Greeting(
            infoRepository = object : InfoRepository {
                override suspend fun getInfo(): Info {
                    delay(1_200)
                    return Info(name = "Test platform")
                }
            }
        )
    )
}
