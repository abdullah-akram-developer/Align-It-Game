package com.trihashstudio.alignit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.trihashstudio.alignit.presentation.MyApp
import com.trihashstudio.alignit.ui.theme.AlignItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlignItTheme {
                Surface(
                    modifier = Modifier,
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp() // ✅ Call your root composable
                }
            }
        }
    }
}
