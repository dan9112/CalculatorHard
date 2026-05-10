package org.example.calculator_hard

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.defaultComponentContext
import org.example.calculator_hard.presentation.App
import org.example.calculator_hard.presentation.RootScreen
import org.example.calculator_hard.presentation.createRootComponent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val rootComponent = createRootComponent(componentContext = defaultComponentContext())

        setContent {
            RootScreen(modifier = Modifier.fillMaxSize(), rootComponent = rootComponent)
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
