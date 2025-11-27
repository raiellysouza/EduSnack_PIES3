package com.example.edusnack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.edusnack.ui.components.BottomNavBar

@Composable
fun PedidoConfirmadoScreen(
    nav: NavController,
    pedidoId: String
) {
    Scaffold(
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text("Pedido Confirmado!", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            Text("Seu pedido foi registrado com sucesso.")
            Text("Código do pedido:")

            Spacer(Modifier.height(8.dp))

            Text(
                pedidoId,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { nav.navigate("home") },
                colors = ButtonDefaults.buttonColors(containerColor = androidx.compose.ui.graphics.Color(0xFF4CAF50))
            ) {
                Text("Voltar ao início")
            }
        }
    }
}
