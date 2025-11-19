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

// ... imports ...

@Composable
fun AppNavGraph(start: String = "welcome") {
    val navController = rememberNavController()
    // ViewModels...

    NavHost(navController = navController, startDestination = start) {

        // ... rotas de login, welcome, etc ...

        composable("homeAluno") { HomeScreen(navController, cardapioVm, carrinhoVm) }

        // --- GARANTA QUE ESSA LINHA DO CANTINEIRO ESTEJA AQUI ---
        composable("homeCantina") { CanteenDashboardScreen(navController) }

        // --- E QUE AS ROTAS DO SEU COLEGA TAMBÉM ESTEJAM AQUI ---
        composable("dailyMenu") { DailyMenuScreen(navController) }
        composable("advanceOrder") { AdvanceOrderScreen(navController) }
        composable("studentAccount") { StudentAccountScreen(navController) }
        composable("canteenInfo") { CanteenInfoScreen(navController) }

        composable(
            route = "detalhes/{itemId}",
            arguments = listOf(navArgument("itemId") { type = NavType.StringType })
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getString("itemId") ?: ""
            ItemDetailsScreen(navController, itemId = itemId)
        }

        composable("carrinho") { CarrinhoScreen(navController) }

        // Rota de confirmação que veio da develop
        composable(
            route = "pedidoConfirmado/{pedidoId}",
            arguments = listOf(navArgument("pedidoId") { type = NavType.StringType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getString("pedidoId") ?: ""
            // Verifique se o nome da tela abaixo bate com o que veio da develop (OrderConfirmationScreen ou PedidoConfirmadoScreen)
            OrderConfirmationScreen(navController, pedidoId)
        }
    }
}