package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.edusnack.model.Aluno
import com.example.edusnack.model.Pedido
import com.example.edusnack.ui.components.BottomNavBar
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.viewmodel.ParentViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// --- Cores Base ---
val LightBackground = Color(0xFFF5F5F5)

@Composable
fun ParentAccountScreen(nav: NavController, vm: ParentViewModel = viewModel()) {
    val user by vm.user.collectAsState()
    val children by vm.children.collectAsState()
    val recentOrders by vm.recentOrders.collectAsState()
    val loading by vm.loading.collectAsState()

    Scaffold(
        topBar = {
            // Header
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
                    text = "Conta do Responsável",
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
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(LightBackground)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- Seção do Perfil do Pai/Mãe ---
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    // Foto do Responsável
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFCCBC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Foto do Responsável",
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFF8D6E63)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = user?.nome ?: "Carregando...",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // --- Seção: Meus Dependentes ---
                item {
                    ParentSectionTitle(text = "Meus Dependentes")
                }

                if (children.isEmpty()) {
                    item {
                        Text("Nenhum dependente encontrado.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    items(children) { child ->
                        ChildDependentRow(child)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // --- Seção: Histórico de Transações ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    ParentSectionTitle(text = "Histórico de Transações")
                }

                if (recentOrders.isEmpty()) {
                    item {
                        Text("Nenhuma transação recente.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    items(recentOrders) { order ->
                        TransactionSummaryRow(order)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

// --- Componentes Auxiliares ---

@Composable
fun ParentSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Start
    )
}

@Composable
fun ChildDependentRow(child: Aluno) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar do Filho
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE0B2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de ${child.nomeCompleto}",
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF795548)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Dados do Filho
        Column {
            Text(
                text = child.nomeCompleto,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Turma: ${child.anoOuTurma}",
                fontSize = 14.sp,
                color = Color(0xFF4CAF50) // Usando cor verde similar ao AppGreen anterior
            )
        }
    }
}

@Composable
fun TransactionSummaryRow(order: Pedido) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateStr = sdf.format(order.data.toDate())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = order.itens.joinToString(", ") { it.nome },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = order.alunoNome,
                fontSize = 14.sp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = dateStr,
                fontSize = 12.sp,
                color = Color(0xFF4CAF50)
            )
        }
        Text(
            text = "R$ ${String.format("%.2f", order.total)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ParentAccountScreenPreview() {
    ParentAccountScreen(nav = rememberNavController())
}
