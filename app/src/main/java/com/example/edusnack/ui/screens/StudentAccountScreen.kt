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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.viewmodel.StudentAccountViewModel
import com.example.edusnack.viewmodel.StudentTransaction

@Composable
fun StudentAccountScreen(nav: NavController, vm: StudentAccountViewModel = viewModel()) {
    val user by vm.user.collectAsState()
    val alunoInfo by vm.alunoInfo.collectAsState()
    val transactions by vm.transactions.collectAsState()
    val loading by vm.loading.collectAsState()

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
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF4CAF50))
            }
        } else {
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
                        model = user?.fotoUrl ?: "https://placehold.co/200x200/FFCCBC/ffffff?text=${user?.nome?.take(2) ?: "AL"}",
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFCCBC))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = user?.nome ?: "Nome não disponível",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Text(
                        text = "Saldo: R$ ${String.format("%.2f", alunoInfo?.saldo ?: 0.0)}",
                        fontSize = 18.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = "Turma: ${alunoInfo?.anoOuTurma ?: "Não informada"}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 4.dp)
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

                if (transactions.isEmpty()) {
                    item {
                        Text(
                            text = "Nenhuma transação encontrada.",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(top = 20.dp)
                        )
                    }
                } else {
                    items(transactions) { transaction ->
                        TransactionItem(transaction)
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFEEEEEE)
                        )
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: StudentTransaction) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
                color = Color(0xFF4CAF50),
                fontWeight = FontWeight.Medium
            )
        }

        val sign = if (transaction.amount > 0) "+" else ""
        val formattedPrice = "R$%.2f".format(transaction.amount).replace('.', ',')

        Text(
            text = "$sign$formattedPrice",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (transaction.amount > 0) Color(0xFF4CAF50) else Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StudentAccountScreenPreview() {
    StudentAccountScreen(nav = rememberNavController())
}
