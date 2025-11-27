package com.example.edusnack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.edusnack.ui.screens.*

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {

        composable("welcome") {
            WelcomeScreen(navController)
        }

        composable("login") {
            LoginScreen(navController)
        }

        composable("register") {
            RegisterScreen(navController)
        }

        composable("forgot") {
            ForgotPasswordScreen(navController)
        }

        composable("homeAluno") { /* Próxima camada */ }

        composable("homeCantina") { /* Próxima camada */ }
    }
}
