package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.viewmodel.CarrinhoViewModel
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun CarrinhoScreen(nav: NavController, usuarioId: String, vm: CarrinhoViewModel) {
    val itens by vm.itens.collectAsState()
    Scaffold(bottomBar = { BottomNavBar(nav) }) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 24.dp)) {
            Spacer(Modifier.height(20.dp))
            Text("Carrinho", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(20.dp))
            if (itens.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Seu carrinho está vazio.") }
                return@Scaffold
            }
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(itens.size) { i ->
                    val item = itens[i]
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.item.nome); Text("Qtd: ${item.quantidade}"); Text("Subtotal: R$ %.2f".format(item.subtotal()))
                        }
                        Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                            Text("+", modifier = Modifier.size(32.dp).background(Color(0xFFE9F2E8), RoundedCornerShape(6.dp)).clickable { vm.adicionar(item.item) }.padding(8.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("-", modifier = Modifier.size(32.dp).background(Color(0xFFE9F2E8), RoundedCornerShape(6.dp)).clickable { vm.remover(item.item) }.padding(8.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Total: R$ %.2f".format(vm.total()), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            Button(onClick = { vm.finalizarCompra(usuarioId) { id -> if (id != null) nav.navigate("pedidoConfirmado/$id") } }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))) {
                Text("Finalizar Pedido")
            }
        }
    }
}
