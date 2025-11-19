package com.example.edusnack.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edusnack.ui.components.CanteenBottomNavBar
import com.example.edusnack.ui.components.SummaryDashboardCard
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GrayBackground
import com.example.edusnack.ui.theme.GreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenDashboardScreen(nav: NavController) {
    Scaffold(
        containerColor = GrayBackground,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Painel da Cantina",
                        fontWeight = FontWeight.Bold,
                        color = DarkText,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = GrayBackground
                )
            )
        },
        bottomBar = { CanteenBottomNavBar(nav) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            // --- Título da Seção Resumo ---
            item {
                Text(
                    text = "Resumo do Dia",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }

            // --- CARDS DE RESUMO ---
            item {
                SummaryDashboardCard(
                    title = "Cardápios ativos hoje",
                    value = "2",
                    description = "Gerencie os cardápios ativos para hoje",
                    imageUrl = "https://example.com/menu_icon.png" // Coloque sua URL ou Drawable
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SummaryDashboardCard(
                    title = "Pedidos pendentes",
                    value = "5",
                    description = "Pedidos aguardando preparo",
                    imageUrl = "https://example.com/burger_icon.png"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SummaryDashboardCard(
                    title = "Pedidos prontos para retirada",
                    value = "3",
                    description = "Pedidos prontos para serem retirados",
                    imageUrl = "https://example.com/bag_icon.png"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SummaryDashboardCard(
                    title = "Total de vendas do dia",
                    value = "R$ 150,00",
                    description = "Vendas totais de hoje",
                    imageUrl = "https://example.com/plant_icon.png"
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            // --- Título da Seção Ações ---
            item {
                Text(
                    text = "Ações",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // --- LISTA DE BOTÕES ---
            item {
                // Botão Principal (Verde)
                ActionButton(
                    text = "Criar cardápio",
                    backgroundColor = GreenPrimary,
                    textColor = DarkText,
                    onClick = { nav.navigate("create_menu") }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                ActionButton(
                    text = "Ver pedidos",
                    backgroundColor = Color(0xFFE8F5E9), // Verde bem claro/Cinza
                    textColor = DarkText,
                    onClick = { /* nav.navigate("view_orders") */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                ActionButton(
                    text = "Relatórios",
                    backgroundColor = Color(0xFFE8F5E9),
                    textColor = DarkText,
                    onClick = { /* nav.navigate("reports") */ }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                ActionButton(
                    text = "Configurações",
                    backgroundColor = Color(0xFFE8F5E9),
                    textColor = DarkText,
                    onClick = { /* nav.navigate("settings") */ }
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Componente local auxiliar para os botões ficarem padronizados
@Composable
fun ActionButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}