package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Numbers // Ou Hashtag se disponível
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.edusnack.ui.components.BottomNavBar

// Reutilizando cores
val SuccessBackgroundGreen = Color(0xFFE8F5E9) // Fundo dos cards e botão
val AppGreenText = Color(0xFF4CAF50)

@Composable
fun RechargeSuccessScreen(nav: NavController) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = { nav.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.Black
                    )
                }
                Text(
                    text = "Recarga Confirmada",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Título de Sucesso
            Text(
                text = "Recarga realizada com\nsucesso!",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo
            Text(
                text = "O valor de R$ 20,00 foi adicionado ao saldo de Lucas.",
                fontSize = 16.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Card 1: Saldo Atual
            SuccessInfoCard(
                icon = Icons.Default.AccountBalanceWallet,
                value = "R$ 55,00",
                label = "Saldo atual"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Referência
            SuccessInfoCard(
                icon = Icons.Default.Numbers, // Ícone genérico de número
                value = "123456789",
                label = "Referência"
            )

            Spacer(modifier = Modifier.weight(1f))

            // Botão Fazer Nova Recarga
            Button(
                onClick = {
                    // Volta para a tela de recarga (pop) ou limpa os campos
                    nav.popBackStack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SuccessBackgroundGreen
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    text = "Fazer Nova Recarga",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Componente para os Cards (Ícone + Texto)
@Composable
fun SuccessInfoCard(
    icon: ImageVector,
    value: String,
    label: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SuccessBackgroundGreen, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Ícone
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Textos
        Column {
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = AppGreenText,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RechargeSuccessPreview() {
    RechargeSuccessScreen(nav = rememberNavController())
}