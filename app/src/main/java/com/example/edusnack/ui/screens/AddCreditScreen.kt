package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.edusnack.ui.components.BottomNavBar

// Cores extraídas da imagem
val InputBackground = Color(0xFFE8F5E9) // Verde bem clarinho dos campos
val PrimaryGreen = Color(0xFF00E676) // Verde vibrante do botão principal
val TextGreen = Color(0xFF4CAF50) // Verde dos textos (R$ 0,00)

@Composable
fun AddCreditScreen(nav: NavController) {
    // Estados para controlar os valores da tela
    var selectedAmount by remember { mutableStateOf("0,00") }
    var selectedPaymentMethod by remember { mutableStateOf("Pix") }

    // Função auxiliar para definir valor ao clicar nos chips
    fun setAmount(value: String) {
        selectedAmount = value
    }

    Scaffold(
        topBar = {
            // Header: Botão Voltar e Título
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
                    text = "Adicionar Crédito",
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
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // --- 1. SELECIONE O ALUNO ---
            Text(
                text = "Selecione o Aluno",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Campo Fake de Select (Dropdown)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(InputBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(text = "Selecione", color = Color.Black, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 2. VALOR A ADICIONAR ---
            Text(
                text = "Valor a Adicionar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Campo de Valor Grande
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(InputBackground, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "R$ ",
                        color = TextGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    // Aqui seria um TextField real, usei Text para simplificar o visual
                    Text(
                        text = selectedAmount,
                        color = TextGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chips de Valor Rápido (R$10, R$20, R$50)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickAmountChip(label = "R$10", onClick = { setAmount("10,00") })
                QuickAmountChip(label = "R$20", onClick = { setAmount("20,00") })
                QuickAmountChip(label = "R$50", onClick = { setAmount("50,00") })
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- 3. MÉTODO DE PAGAMENTO ---
            Text(
                text = "Método de Pagamento",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Opção Pix Selecionada
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, InputBackground, RoundedCornerShape(8.dp)) // Borda sutil
                    .background(Color.White, RoundedCornerShape(8.dp)) // Fundo branco na imagem a borda é o destaque, ou fundo muito claro
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pix",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    // Radio Button Simulado (Círculo Verde)
                    Icon(
                        imageVector = Icons.Default.RadioButtonChecked, // Requer import correto ou icone customizado
                        contentDescription = "Selecionado",
                        tint = PrimaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // Empurra o botão para o final

            // --- 4. BOTÃO CONFIRMAR ---
            Button(
                onClick = { /* Lógica de confirmar pagamento */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryGreen
                )
            ) {
                Text(
                    text = "Confirmar Adicionar Crédito",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Componente para os botões pequenos de valor (R$10, R$20...)
@Composable
fun QuickAmountChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .width(70.dp) // Largura fixa ou wrapContent
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(InputBackground)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

// Import necessário para o ícone de Radio Button, caso não tenha o import automático:
// import androidx.compose.material.icons.filled.RadioButtonChecked

@Preview(showBackground = true)
@Composable
fun AddCreditScreenPreview() {
    AddCreditScreen(nav = rememberNavController())
}