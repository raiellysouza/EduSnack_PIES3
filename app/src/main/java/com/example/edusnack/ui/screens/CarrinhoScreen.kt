package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.viewmodel.CarrinhoViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun CarrinhoScreen(
    nav: NavController,
    usuarioId: String,
    vm: CarrinhoViewModel
) {
    val itens by vm.itens.collectAsState()

    // Saldo (se quiser manter)
    val db = remember { FirebaseFirestore.getInstance() }
    var saldoDisponivel by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(usuarioId) {
        saldoDisponivel = try {
            val docUsuarios = db.collection("usuarios").document(usuarioId).get().await()
            docUsuarios.getDouble("saldo") ?: docUsuarios.getDouble("carteira")
        } catch (_: Exception) {
            null
        }
    }

    val total = remember(itens) { vm.total() }

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
                    text = "Finalizar Compra",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->

        if (itens.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Seu carrinho está vazio.", color = Color.Gray)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { nav.navigate("dailyMenu") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Adicionar opções", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text("Carrinho", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(itens) { ci ->
                    val nome = ci.item.nome
                    val qtd = ci.quantidade
                    val subtotal = ci.subtotal()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Esquerda: nome + qtd
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = nome, fontSize = 14.sp, color = Color.Black)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${qtd}x",
                                fontSize = 12.sp,
                                color = Color(0xFF4CAF50)
                            )
                        }

                        // Direita: preço e controles
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = money(subtotal),
                                fontSize = 14.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(end = 12.dp)
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                SmallSquareButton(text = "+") { vm.adicionar(ci.item) }
                                SmallSquareButton(text = "–") { vm.remover(ci.item) }
                            }
                        }
                    }

                    HorizontalDivider(color = Color(0xFFF2F2F2), thickness = 1.dp)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text("Resumo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))

            SummaryRow(label = "Total", value = money(total))
            Spacer(Modifier.height(8.dp))
            SummaryRow(
                label = "Saldo Disponível",
                value = saldoDisponivel?.let { money(it) } ?: "—"
            )

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = { nav.navigate("dailyMenu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Adicionar mais opções")
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    vm.finalizarCompra(usuarioId) { id ->
                        if (id != null) nav.navigate("pedidoConfirmado/$id")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirmar Compra", color = Color.Black, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = Color(0xFF4CAF50))
        Text(text = value, fontSize = 12.sp, color = Color.Black)
    }
}

@Composable
private fun SmallSquareButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(Color(0xFFE9F2E8), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = text, color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}

private fun money(v: Double): String =
    "R$ %.2f".format(v).replace('.', ',')
