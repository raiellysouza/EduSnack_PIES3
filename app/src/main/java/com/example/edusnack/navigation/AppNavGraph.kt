package com.example.edusnack.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.edusnack.ui.screens.*
import com.example.edusnack.viewmodel.CardapioViewModel
import com.example.edusnack.viewmodel.CarrinhoViewModel

@Composable
fun AppNavGraph(start: String = "welcome") {
    val navController = rememberNavController()

    // create viewmodels here so they are shared between composables
    val cardapioVm: CardapioViewModel = viewModel()
    val carrinhoVm: CarrinhoViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = start
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

        composable("home") {
            HomeScreen(navController, cardapioVm = cardapioVm, carrinhoVm = carrinhoVm)
        }

        composable(
            route = "detalhes/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            ItemDetailsScreen(navController, itemId, cardapioVm = cardapioVm, carrinhoVm = carrinhoVm)
        }

        composable(
            route = "carrinho",
        ) {
            // assume userId will be resolved elsewhere; pass empty for now or fetch current user
            CarrinhoScreen(navController, usuarioId = "", vm = carrinhoVm)
        }

        composable(
            route = "pedidoConfirmado/{pedidoId}",
            arguments = listOf(navArgument("pedidoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getString("pedidoId") ?: ""
            PedidoConfirmadoScreen(navController, pedidoId)
        }

        composable("conta") {
            ContaScreen(navController)
        }
    }
}
