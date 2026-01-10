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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edusnack.viewmodel.AuthViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(nav: NavController, vm: AuthViewModel = viewModel()) {

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var passVisible by remember { mutableStateOf(false) }
    // O estado inicial é "aluno"
    var tipo by remember { mutableStateOf("aluno") }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    // Navigation after success handled by viewModel state
    if (success) {
        val destino = when (tipo) {
            "cantina" -> "homeCantina"
            "responsavel" -> "myDependents"
            else -> "homeAluno"
        }

        nav.navigate(destino) {
            popUpTo("login") { inclusive = true }
        }
        vm.clearState()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Entrar", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(24.dp))

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

            Spacer(Modifier.height(24.dp))

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
                visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passVisible = !passVisible }) {
                        Icon(imageVector = if (passVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = "Alternar visibilidade")
                    }
                },
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
                listOf("Aluno", "Cantina", "Responsável").forEach { item ->
                    val itemKey = when(item) {
                        "Cantina" -> "cantina"
                        "Responsável" -> "responsavel"
                        else -> "aluno"
                    }
                    val isSelected = tipo == itemKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSelected) Color(0xFF4CAF50) else Color(0xFFE9F2E8),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { tipo = itemKey }
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

            Spacer(Modifier.height(24.dp))

            // Botão Entrar
            Button(
                onClick = { vm.login(email, pass, tipo) },
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
}