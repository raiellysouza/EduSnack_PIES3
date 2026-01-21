package com.example.edusnack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edusnack.navigation.AppNavGraph
import com.example.edusnack.ui.theme.EduSnackTheme
import com.example.edusnack.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Instância única do ThemeViewModel para todo o app
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            EduSnackTheme(darkTheme = isDarkMode) {
                // Passamos o ViewModel para o Grafo de Navegação
                AppNavGraph(themeViewModel = themeViewModel)
            }
        }
    }
}
