package com.example.edusnack.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edusnack.viewmodel.AuthViewModel

@Composable
fun AuthScreen(viewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") }
        )
        Spacer(Modifier.height(12.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") }
        )
        Spacer(Modifier.height(20.dp))
        Button(onClick = { viewModel.login(email, password) }) {
            Text("Entrar")
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { viewModel.register(email, password) }) {
            Text("Registrar")
        }
        Spacer(Modifier.height(12.dp))
        authState?.let {
            Text(text = "Status: $it")
        }
    }
}
