package com.example.edusnack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.edusnack.model.TipoUsuario
import com.example.edusnack.viewmodel.AuthViewModel

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    onLoginSuccess: (TipoUsuario) -> Unit,
    onGoToRegister: () -> Unit = {}
) {
    val uiState by viewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedTipo by remember { mutableStateOf(TipoUsuario.ALUNO) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Bem-vindo de volta!", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // Simple role selector
            val options = listOf(TipoUsuario.ALUNO, TipoUsuario.CANTINEIRO, TipoUsuario.RESPONSAVEL)
            options.forEach { tipo ->
                Button(onClick = { selectedTipo = tipo }, colors = if (selectedTipo == tipo) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors()) {
                    Text(tipo.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = {
            viewModel.login(email, password, selectedTipo) { success ->
                if (success) onLoginSuccess(selectedTipo)
            }
        }, modifier = Modifier.fillMaxWidth()) { Text("Entrar") }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { viewModel.sendReset(email) { ok, _ -> /* show toast */ } }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Esqueceu a senha?")
        }

        Spacer(modifier = Modifier.height(12.dp))
        uiState?.let {
            when (it) {
                is com.example.edusnack.viewmodel.AuthState.Loading -> Text("Carregando...")
                is com.example.edusnack.viewmodel.AuthState.Error -> Text("Erro: ${(it as com.example.edusnack.viewmodel.AuthState.Error).message}")
                else -> { /* ignore */ }
            }
        }
    }
}
