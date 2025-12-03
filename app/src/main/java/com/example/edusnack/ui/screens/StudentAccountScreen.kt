package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.edusnack.ui.components.BottomNavBar

// Modelo de dados para as transações (Localizado aqui para facilitar a cópia)
data class Transaction(
    val title: String,
    val date: String,
    val type: String, // Ex: "Compra na Cafeteria"
    val amount: Double
)

@Composable
fun StudentAccountScreen(nav: NavController) {
    // Dados simulados idênticos à imagem enviada
    val transactions = listOf(
        Transaction("Almoço", "15/03/24", "Compra na Cafeteria", -7.50),
        Transaction("Depósito", "10/03/24", "Pagamento Online", 50.00),
        Transaction("Depósito", "10/03/24", "Pagamento Online", 50.00),
        Transaction("Depósito", "10/03/24", "Pagamento Online", 50.00),
        Transaction("Depósito", "10/03/24", "Pagamento Online", 50.00),
        Transaction("Café da Manhã", "08/03/24", "Compra na Cafeteria", -3.25)
    )

    Scaffold(
        topBar = {
            // Top Bar Personalizada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                // Botão Voltar
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

                // Título Centralizado
                Text(
                    text = "Conta",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                AsyncImage(
                    model = "https://placehold.co/200x200/FFCCBC/ffffff?text=IS", // URL Placeholder
                    contentDescription = "Foto de perfil",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFCCBC)) // Cor de fundo caso a imagem demore
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Isabela Souza",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "ID do Aluno: 123456",
                    fontSize = 14.sp,
                    color = Color(0xFF4CAF50), // Verde do App
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(40.dp))
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Histórico de Transações",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            // --- LISTA DE TRANSAÇÕES ---
            items(transactions) { transaction ->
                TransactionItem(transaction)
                // Linha divisória fina entre itens (opcional, mas ajuda na visualização)
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    thickness = 0.5.dp,
                    color = Color(0xFFEEEEEE)
                )
            }

            // Espaçamento final para o conteúdo não ficar escondido atrás da BottomBar
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

// Sub-componente para renderizar cada linha da transação
@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lado Esquerdo: Título, Data e Tipo
        Column {
            Text(
                text = "${transaction.title} - ${transaction.date}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = transaction.type,
                fontSize = 14.sp,
                color = Color(0xFF4CAF50), // Verde característico
                fontWeight = FontWeight.Medium
            )
        }

        // Lado Direito: Valor Formatado
        // Lógica: Se for positivo põe "+", troca ponto por vírgula
        val sign = if (transaction.amount > 0) "+" else ""
        val formattedPrice = "R$%.2f".format(transaction.amount).replace('.', ',')

        Text(
            text = "$sign$formattedPrice",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StudentAccountScreenPreview() {
    StudentAccountScreen(nav = rememberNavController())
}