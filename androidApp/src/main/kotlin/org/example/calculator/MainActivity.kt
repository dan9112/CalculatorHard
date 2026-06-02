package org.example.calculator

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.defaultComponentContext
import org.example.calculator.presentation.RootScreen
import org.example.calculator.shared.createRootComponent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val rootComponent = createRootComponent(componentContext = defaultComponentContext())

        setContent {
            RootScreen(
                modifier = Modifier.fillMaxSize(),
                rootComponent = rootComponent,
            )
        }
    }
}

// @Preview
// @Composable
// fun AppAndroidPreview() {
//
// }
