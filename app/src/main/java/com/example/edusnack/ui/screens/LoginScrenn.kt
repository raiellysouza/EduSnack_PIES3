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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edusnack.viewmodel.AuthViewModel

@Composable
fun LoginScreen(nav: NavController, vm: AuthViewModel = viewModel()) {

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("aluno") }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    if (success) {
        nav.navigate("homeAluno") {
            popUpTo("login") { inclusive = true }
        }
        vm.clearState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(60.dp))

        Text("Cafeteria Escolar", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Text("Bem-vindo de volta!", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(32.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = { Text("E-mail") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE9F2E8),
                unfocusedContainerColor = Color(0xFFE9F2E8)
            )
        )

        Spacer(Modifier.height(16.dp))

        TextField(
            value = pass,
            onValueChange = { pass = it },
            placeholder = { Text("Senha") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE9F2E8),
                unfocusedContainerColor = Color(0xFFE9F2E8)
            )
        )

        Spacer(Modifier.height(20.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Aluno", "Cantineiro", "Responsável").forEach { item ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                        .background(
                            if (tipo == item.lowercase()) Color(0xFF4CAF50)
                            else Color(0xFFE9F2E8),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { tipo = item.lowercase() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            "Esqueceu a senha?",
            modifier = Modifier
                .align(Alignment.Start)
                .clickable { nav.navigate("forgot") }
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { vm.login(email, pass) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Entrar")
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = { nav.navigate("register") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Criar conta")
        }

        error?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
