package com.example.edusnack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edusnack.viewmodel.AuthViewModel

@Composable
fun LoginScreen(nav: NavController, vm: AuthViewModel = viewModel()) {

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    // O estado inicial é "aluno", mas muda ao clicar nos botões
    var tipo by remember { mutableStateOf("aluno") }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    // --- LÓGICA DE NAVEGAÇÃO CORRIGIDA ---
    if (success) {
        // Verifica qual tipo foi selecionado na UI antes de navegar
        val destino = when (tipo) {
            "cantineiro" -> "homeCantina" // Vai para o Painel da Cantina
            "responsável" -> "homeResponsavel" // Caso crie essa tela no futuro
            else -> "homeAluno" // Padrão: vai para Home do Aluno
        }

        nav.navigate(destino) {
            popUpTo("login") { inclusive = true } // Remove login da pilha
        }
        vm.clearState() // Limpa o estado de sucesso para não navegar de novo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White) // Garante fundo branco
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(60.dp))

        Text(
            "Cafeteria Escolar",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "Bem-vindo de volta!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(32.dp))

        // Campo de Email
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE9F2E8),
                unfocusedContainerColor = Color(0xFFE9F2E8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(Modifier.height(16.dp))

        // Campo de Senha
        TextField(
            value = pass,
            onValueChange = { pass = it },
            placeholder = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(), // Adicionei para esconder a senha
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE9F2E8),
                unfocusedContainerColor = Color(0xFFE9F2E8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(Modifier.height(20.dp))

        // --- SELETOR DE TIPO DE USUÁRIO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Aluno", "Cantineiro", "Responsável").forEach { item ->
                val isSelected = tipo == item.lowercase()
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = if (isSelected) Color(0xFF4CAF50) else Color(0xFFE9F2E8),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { tipo = item.lowercase() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        color = if (isSelected) Color.White else Color.Black,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Esqueceu a senha?",
            color = Color(0xFF4CAF50),
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { nav.navigate("forgot") }
        )

        Spacer(Modifier.height(32.dp))

        // Botão Entrar
        Button(
            onClick = { vm.login(email, pass) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            if (loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Entrar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(16.dp))

        // Botão Criar Conta
        OutlinedButton(
            onClick = { nav.navigate("register") },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Text("Criar conta", fontWeight = FontWeight.Bold)
        }

        // Mensagem de Erro
        error?.let { msg ->
            Spacer(Modifier.height(16.dp))
            Text(msg, color = MaterialTheme.colorScheme.error)
        }
    }
}