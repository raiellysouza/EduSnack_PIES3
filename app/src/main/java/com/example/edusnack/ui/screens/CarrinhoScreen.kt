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
    val error by vm.error.collectAsState()

    val db = remember { FirebaseFirestore.getInstance() }
    var saldoDisponivel by remember { mutableStateOf<Double?>(null) }
    var processing by remember { mutableStateOf(false) }

    LaunchedEffect(usuarioId) {
        saldoDisponivel = try {
            // Alterado para "usuarios" para manter consistência com o restante do app
            val docUsuarios = db.collection("usuarios").document(usuarioId).get().await()
            docUsuarios.getDouble("saldo")
        } catch (_: Exception) {
            null
        }
    }

    val total = remember(itens) { vm.total() }
    val saldoInsuficiente = saldoDisponivel != null && saldoDisponivel!! < total

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        error?.let {
            snackbarHostState.showSnackbar(it)
            vm.clearError()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = { nav.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                        contentDescription = "Voltar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Finalizar Compra",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
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
                    .background(MaterialTheme.colorScheme.background),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Seu carrinho está vazio.", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                "Carrinho", 
                fontSize = 16.sp, 
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                items(itens) { ci ->
                    val nome = ci.item.nome
                    val subtotal = ci.subtotal()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = nome, fontSize = 14.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(4.dp))
                            
                            if (ci.diasReserva.isNotEmpty()) {
                                Text(
                                    text = "Reservado: ${ci.diasReserva.joinToString(", ")}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = money(subtotal),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(end = 12.dp),
                                fontWeight = FontWeight.Bold
                            )

                            SmallSquareButton(text = "–") { vm.remover(ci.item) }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text("Resumo", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            Spacer(Modifier.height(12.dp))

            SummaryRow(label = "Total Semanal", value = money(total))
            Spacer(Modifier.height(8.dp))
            SummaryRow(
                label = "Meu Saldo",
                value = saldoDisponivel?.let { money(it) } ?: "—"
            )

            if (saldoInsuficiente) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Saldo insuficiente para realizar esta compra.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (saldoInsuficiente) return@Button
                    processing = true
                    vm.finalizarCompra(usuarioId) { id ->
                        processing = false
                        if (id != null) nav.navigate("pedidoConfirmado/$id")
                    }
                },
                enabled = !saldoInsuficiente && !processing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (saldoInsuficiente) Color.Gray else Color(0xFF00E676)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (processing) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
                } else {
                    Text(
                        if (saldoInsuficiente) "Saldo Insuficiente" else "Confirmar Compra",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
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
        Text(text = value, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun SmallSquareButton(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        TextButton(
            onClick = onClick,
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Text(text = text, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
        }
    }
}

private fun money(v: Double): String = "R$ %.2f".format(v).replace('.', ',')
