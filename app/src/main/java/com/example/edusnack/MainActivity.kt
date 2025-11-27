package com.example.edusnack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.edusnack.navigation.AppNavGraph
import com.example.edusnack.ui.theme.EduSnackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EduSnackTheme {
                AppNavGraph()
            }
        }
    }
}
