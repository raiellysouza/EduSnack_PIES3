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
fun ForgotPasswordScreen(nav: NavController, vm: AuthViewModel = viewModel()) {

    var email by remember { mutableStateOf("") }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    if (success) {
        nav.popBackStack()
        vm.clearState()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
    ) {

        Spacer(Modifier.height(60.dp))

        Text("Redefinir senha", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(16.dp))

        Text(
            "Esqueceu sua senha?\nInsira o e-mail associado à sua conta e enviaremos instruções para redefinir sua senha.",
            style = MaterialTheme.typography.bodyMedium
        )

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

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { vm.forgotPassword(email) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),

        ) {
            Text("Enviar")
        }

        error?.let {
            Spacer(Modifier.height(16.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}
