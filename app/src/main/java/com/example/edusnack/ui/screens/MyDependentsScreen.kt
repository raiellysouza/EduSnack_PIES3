package com.example.edusnack.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.edusnack.data.AuthRepository
import com.example.edusnack.model.User
import com.example.edusnack.ui.components.BottomNavBar
import kotlinx.coroutines.launch

@Composable
fun MyDependentsScreen(nav: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val authRepo = remember { AuthRepository() }
    
    var responsavelId by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val user = authRepo.getUser()
        responsavelId = user?.id
        isLoading = false
    }

    // Agora busca os dependentes diretamente da coleção 'usuarios' (tipo User)
    val dependents by responsavelId?.let { id ->
        authRepo.getDependentesByUser(id).collectAsState(initial = emptyList())
    } ?: remember { mutableStateOf(emptyList<User>()) }

    var showAddDependentModal by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Meus Dependentes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
        },
        bottomBar = { BottomNavBar(nav) }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = AppGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color.White)
                    .padding(horizontal = 24.dp)
            ) {
                items(dependents) { dependent ->
                    DependentUserItem(dependent)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                if (dependents.isEmpty()) {
                    item {
                        Text(text = "Nenhum dependente cadastrado.", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
                item { Text(text = "Opções", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp)) }

                item {
                    OptionItem(text = "Adicionar Fundos", icon = Icons.Default.Add, onClick = { })
                }
                item {
                    OptionItem(text = "Adicionar Novo Dependente", icon = Icons.Default.PersonAdd, onClick = { showAddDependentModal = true })
                }
            }
        }

        if (showAddDependentModal) {
            AddDependentUserDialog(
                onDismiss = { showAddDependentModal = false },
                onConfirm = { nome, email, senha ->
                    responsavelId?.let { resId ->
                        scope.launch {
                            // Cria apenas o User com tipo 'aluno' e o vínculo do responsável
                            val result = authRepo.register(nome, email, senha, "aluno", responsavelId = resId)
                            
                            if (result.isSuccess) {
                                showAddDependentModal = false
                                Toast.makeText(context, "Dependente cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Erro: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun OptionItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(LightGreenBackground, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.Black)
    }
}

@Composable
fun AddDependentUserDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth().padding(16.dp), elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Novo Dependente", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.padding(bottom = 16.dp))
                
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome Completo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail do Aluno") }, modifier = Modifier.fillMaxWidth(), singleLine = true, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, modifier = Modifier.fillMaxWidth(), singleLine = true, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password))
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { if (name.isNotBlank() && email.isNotBlank() && password.length >= 6) { onConfirm(name, email, password) } },
                        colors = ButtonDefaults.buttonColors(containerColor = AppGreen),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Salvar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun DependentUserItem(user: User) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFFFCCBC)), contentAlignment = Alignment.Center) {
            Text(text = user.nome.firstOrNull()?.toString()?.uppercase() ?: "?", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = user.nome, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = user.email, fontSize = 14.sp, color = Color.Gray)
        }
    }
}
