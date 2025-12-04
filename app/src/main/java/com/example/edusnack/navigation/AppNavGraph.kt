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
    val cardapioVm: CardapioViewModel = viewModel()
    val carrinhoVm: CarrinhoViewModel = viewModel()

    NavHost(navController = navController, startDestination = start) {
        composable("welcome") { WelcomeScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("forgot") { ForgotPasswordScreen(navController) }

        composable("homeAluno") { HomeScreen(navController, cardapioVm = cardapioVm, carrinhoVm = carrinhoVm) }

        composable("dailyMenu") { DailyMenuScreen(navController)  }
        composable("advanceOrder") { AdvanceOrderScreen(navController)  }
        composable("studentAccount") { StudentAccountScreen(navController) }
        composable("canteenInfo") { CanteenInfoScreen(navController) }


        composable("detalhes/{itemId}", arguments = listOf(navArgument("itemId"){ type = NavType.StringType })) { back ->
            val itemId = back.arguments?.getString("itemId") ?: ""
//            ItemDetailsScreen(navController, itemId = itemId, cardapioVm = cardapioVm, carrinhoVm = carrinhoVm)
            ItemDetailsScreen(navController, itemId = itemId)

        }

        composable(
            route = "orderConfirmation/{itemName}/{itemPrice}", // Rota com parâmetros
            arguments = listOf(
                navArgument("itemName") { type = NavType.StringType },
                navArgument("itemPrice") { type = NavType.FloatType } // Float para simplificar na URL
            )
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("itemName") ?: "Item"
            val price = backStackEntry.arguments?.getFloat("itemPrice")?.toDouble() ?: 0.0

            OrderConfirmationScreen(navController, itemName = name, itemPrice = price)
        }

        composable("carrinho") { CarrinhoScreen(navController, usuarioId = "", vm = carrinhoVm) }

        composable("pedidoConfirmado/{pedidoId}", arguments = listOf(navArgument("pedidoId"){ type = NavType.StringType })) { back ->
            val pedidoId = back.arguments?.getString("pedidoId") ?: ""
            PedidoConfirmadoScreen(navController, pedidoId)
        }




//        Rota dos pais
        composable("myDependents") {MyDependentsScreen((navController))}
        composable("addCredit") {AddCreditScreen(navController)}
        composable("purchaseStatement") { PurchaseStatementScreen(navController) }
    }
}
