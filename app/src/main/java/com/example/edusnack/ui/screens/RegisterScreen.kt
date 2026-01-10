package com.example.edusnack.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edusnack.viewmodel.AuthViewModel
import androidx.compose.ui.Alignment
import androidx.compose.foundation.clickable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(nav: NavController, vm: AuthViewModel = viewModel()) {
    // Step 1: user type selection
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Criar Conta", style = MaterialTheme.typography.titleMedium) },
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
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            Text("Cafeteria Escolar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            Text("Escolha o tipo de usuário", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(24.dp))

            var selected by remember { mutableStateOf("") }

            listOf("Aluno", "Cantina", "Responsável").forEach { item ->
                val lower = when(item) {
                    "Cantina" -> "cantina"
                    "Responsável" -> "responsavel"
                    else -> "aluno"
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable { selected = lower },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = if (selected == lower) Color(0xFF4CAF50) else Color(0xFFE9F2E8))
                ) {
                    Box(modifier = Modifier.padding(20.dp)) {
                        Text(item, color = if (selected == lower) Color.White else Color.Black, fontSize = 16.sp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    if (selected.isNotEmpty()) nav.navigate("register_data/$selected")
                },
                enabled = selected.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Próximo")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(onClick = { nav.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                Text("Voltar")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataRegistrationScreen(nav: NavController, tipo: String, vm: AuthViewModel = viewModel()) {
    // New screen: fields vary by tipo
    var nome by remember { mutableStateOf("") }
    // For student-specific fields
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var registrationNumber by remember { mutableStateOf("") }
    val foodRestrictions = remember { mutableStateListOf<String>() }
    var restrictionInput by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var responsavelName by remember { mutableStateOf("") }

    var passVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    var localError by remember { mutableStateOf<String?>(null) }

    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()

    // On success, navigate to appropriate home
    if (success) {
        val destino = when (tipo) {
            "cantina" -> "homeCantina"
            "responsavel" -> "myDependents"
            else -> "homeAluno"
        }
        nav.navigate(destino) {
            popUpTo("register") { inclusive = true }
        }
        vm.clearState()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dados de cadastro", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp)) {

            Spacer(Modifier.height(16.dp))
            Text("Dados de $tipo".replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

            Spacer(Modifier.height(24.dp))

            if (tipo == "aluno") {
                // Student registration sequence
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        placeholder = { Text("Nome") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        placeholder = { Text("Sobrenome") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = registrationNumber,
                    onValueChange = { registrationNumber = it },
                    placeholder = { Text("Matrícula (obrigatório)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(Modifier.height(16.dp))

                // Food restrictions tag input
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = restrictionInput,
                        onValueChange = { restrictionInput = it },
                        placeholder = { Text("Restrição alimentar (opcional)") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(onClick = {
                        val trimmed = restrictionInput.trim()
                        if (trimmed.isNotEmpty() && !foodRestrictions.contains(trimmed)) {
                            foodRestrictions.add(trimmed)
                            restrictionInput = ""
                        }
                    }, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Text("Adicionar")
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Display tags
                if (foodRestrictions.isNotEmpty()) {
                    FlowRow(
                        mainAxisSpacing = 8.dp,
                        crossAxisSpacing = 8.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        foodRestrictions.forEachIndexed { index, tag ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFE9F2E8)
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Text(tag)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(Icons.Default.Close, contentDescription = "Remover", modifier = Modifier.clickable {
                                        foodRestrictions.removeAt(index)
                                    })
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                // Continue with email/password
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
                    visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passVisible = !passVisible }) {
                            Icon(imageVector = if (passVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = "Alternar visibilidade")
                        }
                    },
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
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmVisible = !confirmVisible }) {
                            Icon(imageVector = if (confirmVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = "Alternar visibilidade")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE9F2E8),
                        unfocusedContainerColor = Color(0xFFE9F2E8)
                    )
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        // Basic validation
                        localError = null
                        if (firstName.isBlank()) localError = "Nome é obrigatório"
                        else if (lastName.isBlank()) localError = "Sobrenome é obrigatório"
                        else if (registrationNumber.isBlank()) localError = "Matrícula é obrigatória"
                        else if (email.isBlank()) localError = "E-mail é obrigatório"
                        else if (pass.isBlank()) localError = "Senha é obrigatória"
                        else if (pass != confirm) localError = "As senhas não coincidem"

                        if (localError == null) {
                            // Build profile map for student
                            val fullName = (firstName + " " + lastName).trim()
                            val profile = mapOf(
                                "role" to "STUDENT",
                                "firstName" to firstName,
                                "lastName" to lastName,
                                "registrationNumber" to registrationNumber,
                                "foodRestrictions" to foodRestrictions.toList(),
                                "email" to email
                            )

                            vm.register(fullName, email, pass, tipo, profile)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Criar Conta")
                }

                // Show validation or repo errors
                localError?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                error?.let {
                    Spacer(Modifier.height(16.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(onClick = { nav.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Voltar")
                }

            } else {
                // ...existing code for cantina and responsavel
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
                    visualTransformation = if (passVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passVisible = !passVisible }) {
                            Icon(imageVector = if (passVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = "Alternar visibilidade")
                        }
                    },
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
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmVisible = !confirmVisible }) {
                            Icon(imageVector = if (confirmVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = "Alternar visibilidade")
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE9F2E8),
                        unfocusedContainerColor = Color(0xFFE9F2E8)
                    )
                )

                Spacer(Modifier.height(16.dp))

                if (tipo == "cantina") {
                    TextField(
                        value = phone,
                        onValueChange = { phone = it },
                        placeholder = { Text("Telefone de contato (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFE9F2E8),
                            unfocusedContainerColor = Color(0xFFE9F2E8)
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    TextField(
                        value = responsavelName,
                        onValueChange = { responsavelName = it },
                        placeholder = { Text("Nome do responsável") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFE9F2E8),
                            unfocusedContainerColor = Color(0xFFE9F2E8)
                        )
                    )

                    Spacer(Modifier.height(12.dp))
                    Text("Campos opcionais não bloqueiam o cadastro", style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = {
                        // Basic validation
                        localError = null
                        if (nome.isBlank()) localError = "Nome é obrigatório"
                        else if (email.isBlank()) localError = "E-mail é obrigatório"
                        else if (pass.isBlank()) localError = "Senha é obrigatória"
                        else if (pass != confirm) localError = "As senhas não coincidem"

                        if (localError == null) {
                            // Build profile map for canteen
                            val profile = if (tipo == "cantina") {
                                mapOf(
                                    "canteenName" to nome,
                                    "contactPhone" to phone,
                                    "contactEmail" to email,
                                    "responsibleName" to responsavelName,
                                    "tipo" to "CANTEEN"
                                )
                            } else {
                                mapOf("name" to nome)
                            }

                            vm.register(nome, email, pass, tipo, profile)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !loading,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Criar Conta")
                }

                // Show validation or repo errors
                localError?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                error?.let {
                    Spacer(Modifier.height(16.dp))
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(onClick = { nav.popBackStack() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Voltar")
                }
            }
        }
    }
}
