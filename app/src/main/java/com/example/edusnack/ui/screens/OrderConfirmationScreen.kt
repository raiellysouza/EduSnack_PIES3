package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.edusnack.ui.components.BottomNavBar

@Composable
fun OrderConfirmationScreen(
    nav: NavController,
    itemName: String = "Item Exemplo",
    itemPrice: Double = 0.00
) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = { nav.popBackStack() }, // Volta para a tela anterior
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                }
                Text(
                    text = "Pedido Confirmado",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- CABEÇALHO DO PEDIDO ---
            Text(
                text = "Pedido #123456789",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Seu pedido foi confirmado e está sendo preparado. Você receberá uma notificação quando estiver pronto para retirada.",
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- RESUMO DO PEDIDO (Apenas 1 item) ---
            Text(
                text = "Resumo do Pedido",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Linha do Item
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = itemName, fontSize = 16.sp, color = Color.Black)
                    Text(text = "1x", fontSize = 14.sp, color = Color(0xFF4CAF50)) // "1x" Verde
                }
                Text(
                    text = "R$ %.2f".format(itemPrice).replace('.', ','),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Linha do Total
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total", fontSize = 16.sp, color = Color.Gray)
                Text(
                    text = "R$ %.2f".format(itemPrice).replace('.', ','),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- DETALHES DA RETIRADA ---
            Text(
                text = "Detalhes da Retirada",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Bloco de Horário
            PickupDetailRow(
                title = "Horário",
                value = "12:30 - 13:00",
                icon = Icons.Outlined.AccessTime
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bloco de Local
            PickupDetailRow(
                title = "Local",
                value = "Cafeteria da Escola",
                icon = Icons.Outlined.LocationOn
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Componente auxiliar para as linhas de retirada (Horário/Local) com ícone à direita
@Composable
fun PickupDetailRow(title: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 14.sp, color = Color(0xFF4CAF50)) // Verde
        }
        Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
    }
}

@Preview
@Composable
fun OrderConfirmationPreview() {
    OrderConfirmationScreen(nav = rememberNavController(), itemName = "Sanduíche de Queijo", itemPrice = 8.00)
}