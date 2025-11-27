package com.example.edusnack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edusnack.viewmodel.AuthViewModel
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun RegisterScreen(nav: NavController, vm: AuthViewModel = viewModel()) {

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    if (success) {
        nav.navigate("login") {
            popUpTo("register") { inclusive = true }
        }
        vm.clearState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {

        Spacer(Modifier.height(60.dp))

        Text("Cafeteria Escolar", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        Text("Cadastre-se", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(32.dp))

        TextField(
            value = nome,
            onValueChange = { nome = it },
            placeholder = { Text("Nome") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE9F2E8),
                unfocusedContainerColor = Color(0xFFE9F2E8)
            )
        )

        Spacer(Modifier.height(16.dp))

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


        Spacer(Modifier.height(16.dp))

        TextField(
            value = confirm,
            onValueChange = { confirm = it },
            placeholder = { Text("Confirmar Senha") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE9F2E8),
                unfocusedContainerColor = Color(0xFFE9F2E8)
            )
        )

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { vm.register(nome, email, pass, "aluno") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            enabled = !loading,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Criar Conta")
        }

        error?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
