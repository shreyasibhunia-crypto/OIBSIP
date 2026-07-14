package com.example.stopwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.stopwatch.ui.screens.StopwatchScreen
import com.example.stopwatch.ui.theme.StopwatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Renders content edge-to-edge behind translucent system status/nav bars
        enableEdgeToEdge()
        
        setContent {
            StopwatchTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    StopwatchScreen()
                }
            }
        }
    }
}
