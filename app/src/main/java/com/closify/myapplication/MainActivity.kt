package com.closify.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.closify.myapplication.ui.screens.OnboardingScreen
import com.closify.myapplication.ui.theme.ClosifyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClosifyTheme {
                OnboardingScreen()
            }
        }
    }
}
