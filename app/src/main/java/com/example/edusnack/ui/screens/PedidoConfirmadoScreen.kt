package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edusnack.model.Pedido
import com.example.edusnack.ui.components.BottomNavBar
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun PedidoConfirmadoScreen(
    nav: NavController,
    pedidoId: String
) {
    val db = remember { FirebaseFirestore.getInstance() }

    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var pedido by remember { mutableStateOf<Pedido?>(null) }

    LaunchedEffect(pedidoId) {
        loading = true
        error = null
        pedido = null

        try {
            val doc = db.collection("pedidos").document(pedidoId).get().await()
            pedido = doc.toObject(Pedido::class.java)
            if (pedido == null) error = "Pedido não encontrado."
        } catch (e: Exception) {
            error = e.message ?: "Erro ao carregar pedido."
        } finally {
            loading = false
        }
    }

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

        when {
            loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Erro: $error", color = Color.Red)
                }
            }

            pedido != null -> {
                val p = pedido!!

                val itens = p.itens ?: emptyList()
                val total = p.total ?: itens.sumOf { (it.preco ?: 0.0) * (it.quantidade ?: 0) }
                val codigo = p.codigoRetirada?.takeIf { it.isNotBlank() } ?: pedidoId

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color.White)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Pedido #$codigo",
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

                    Text(
                        text = "Resumo do Pedido",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (itens.isEmpty()) {
                        Text("Sem itens no pedido.", color = Color.Gray)
                    } else {
                        itens.forEach { item ->
                            val nome = item.nome ?: "Item"
                            val qtd = item.quantidade ?: 0
                            val preco = item.preco ?: 0.0
                            val subtotal = preco * qtd

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = nome, fontSize = 16.sp, color = Color.Black)
                                    Text(text = "${qtd}x", fontSize = 14.sp, color = Color(0xFF4CAF50))
                                }
                                Text(
                                    text = formatMoney(subtotal),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total", fontSize = 16.sp, color = Color.Gray)
                        Text(
                            text = formatMoney(total),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = "Detalhes da Retirada",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    PickupDetailRow(
                        title = "Horário",
                        value = "12:30 - 13:00",
                        icon = Icons.Outlined.AccessTime
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PickupDetailRow(
                        title = "Local",
                        value = "Cafeteria da Escola",
                        icon = Icons.Outlined.LocationOn
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Aqui fica aluno: não é "pagar", pq pagar é do cantineiro.
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = { nav.navigate("dailyMenu") },
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text("Escolher mais opções")
                        }

                        Button(
                            onClick = { nav.navigate("homeAluno") },
                            modifier = Modifier
                                .weight(1f)
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text("Voltar ao menu", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun PickupDetailRow(title: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF9F9F9), RoundedCornerShape(8.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 14.sp, color = Color(0xFF4CAF50))
        }
        Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
    }
}

private fun formatMoney(v: Double): String =
    "R$ %.2f".format(v).replace('.', ',')
