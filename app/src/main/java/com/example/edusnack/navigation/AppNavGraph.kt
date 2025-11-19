package com.example.edusnack.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.edusnack.ui.screens.AdvanceOrderScreen
import com.example.edusnack.ui.screens.CanteenDashboardScreen
import com.example.edusnack.ui.screens.CanteenInfoScreen
import com.example.edusnack.ui.screens.CarrinhoScreen
import com.example.edusnack.ui.screens.DailyMenuScreen
import com.example.edusnack.ui.screens.ForgotPasswordScreen
import com.example.edusnack.ui.screens.HomeScreen
import com.example.edusnack.ui.screens.ItemDetailsScreen
import com.example.edusnack.ui.screens.LoginScreen
import com.example.edusnack.ui.screens.PedidoConfirmadoScreen
import com.example.edusnack.ui.screens.RegisterScreen
import com.example.edusnack.ui.screens.StudentAccountScreen
import com.example.edusnack.ui.screens.WelcomeScreen
import com.example.edusnack.viewmodel.CardapioViewModel
import com.example.edusnack.viewmodel.CarrinhoViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavGraph(start: String = "welcome") {
    val navController = rememberNavController()
    // ViewModels compartilhados (opcionais, depende da sua arquitetura)
    val cardapioVm: CardapioViewModel = viewModel()
    val carrinhoVm: CarrinhoViewModel = viewModel()

    NavHost(navController = navController, startDestination = start) {

        // --- ROTAS DE AUTENTICAÇÃO ---
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgot") { ForgotPasswordScreen(navController) }

        // --- ROTAS DO ALUNO ---
        composable("homeAluno") { HomeScreen(navController, cardapioVm, carrinhoVm) }
        composable("dailyMenu") { DailyMenuScreen(navController) }
        composable("advanceOrder") { AdvanceOrderScreen(navController) }
        composable("studentAccount") { StudentAccountScreen(navController) }
        composable("canteenInfo") { CanteenInfoScreen(navController) }

        // --- ROTA DO CANTINEIRO (ADICIONE ISTO AQUI) ---
        composable("homeCantina") {
            CanteenDashboardScreen(navController)
        }

        // --- ROTAS DE PRODUTO E PEDIDO ---
        composable(
            route = "detalhes/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            ItemDetailsScreen(navController, itemId = itemId)
        }

        composable("carrinho") {
            val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            CarrinhoScreen(navController, usuarioId, carrinhoVm)
        }

        composable(
            route = "pedidoConfirmado/{pedidoId}",
            arguments = listOf(navArgument("pedidoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getString("pedidoId") ?: ""
            PedidoConfirmadoScreen(navController, pedidoId) // Ajuste se precisar de mais argumentos
        }
    }
}