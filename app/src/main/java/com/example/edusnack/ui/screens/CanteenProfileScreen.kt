package com.example.edusnack.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.edusnack.R
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.viewmodel.CanteenProfileViewModel
import com.example.edusnack.viewmodel.CanteenProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanteenProfileScreen(nav: NavController, viewModel: CanteenProfileViewModel = viewModel()) {
    val profile by viewModel.profile.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isEditable by viewModel.isEditable.collectAsState()

    var editing by remember { mutableStateOf(false) }

    // Local editable fields
    var canteenName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    var responsibleName by remember { mutableStateOf("") }
    var profileImageUrl by remember { mutableStateOf("") }

    LaunchedEffect(profile) {
        profile?.let {
            canteenName = it.canteenName
            contactPhone = it.contactPhone
            contactEmail = it.contactEmail
            responsibleName = it.responsibleName
            profileImageUrl = it.profileImageUrl
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Conta da Cantina", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    if (isEditable) {
                        IconButton(onClick = { editing = !editing }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                        }
                    }
                }
            )
        },
        bottomBar = { /* No bottom bar here; settings route already uses bottom nav elsewhere */ },
        containerColor = Color.White
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
            return@Scaffold
        }

        if (error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Erro: ${error}", color = Color.Red)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (profileImageUrl.isNotBlank()) {
                        AsyncImage(
                            model = profileImageUrl,
                            contentDescription = "Foto da Cantina",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9))
                        )
                    } else {
                        // Placeholder circle
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE8F5E9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = DarkText, modifier = Modifier.size(56.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Canteen basic info
                OutlinedTextField(
                    value = canteenName,
                    onValueChange = { if (editing) canteenName = it },
                    label = { Text("Nome da Cantina") },
                    readOnly = !editing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = contactPhone,
                    onValueChange = { if (editing) contactPhone = it },
                    label = { Text("Telefone de Contato") },
                    readOnly = !editing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = contactEmail,
                    onValueChange = { if (editing) contactEmail = it },
                    label = { Text("E-mail de Contato") },
                    readOnly = !editing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = responsibleName,
                    onValueChange = { if (editing) responsibleName = it },
                    label = { Text("Nome do Responsável") },
                    readOnly = !editing,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Save button visible when editing
                if (editing) {
                    Button(
                        onClick = {
                            val updated = CanteenProfile(
                                profileImageUrl = profileImageUrl,
                                canteenName = canteenName,
                                contactPhone = contactPhone,
                                contactEmail = contactEmail,
                                responsibleName = responsibleName,
                                employees = profile?.employees ?: emptyList()
                            )
                            viewModel.saveProfile(updated)
                            editing = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Salvar", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Employees section
            item {
                Text(text = "Funcionários", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkText)
                Spacer(modifier = Modifier.height(8.dp))
            }

            val employees = profile?.employees ?: emptyList()
            if (employees.isEmpty()) {
                item {
                    Text(text = "Nenhum funcionário cadastrado", color = Color.Gray)
                }
            } else {
                itemsIndexed(employees) { index, employee ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FFF8), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = employee.name, fontWeight = FontWeight.Bold, color = DarkText)
                            Text(text = employee.role, color = Color.Gray)
                        }

                        if (isEditable) {
                            Row {
                                Text(text = "Editar", color = GreenPrimary, modifier = Modifier.clickable {
                                    // Open a simple inline editor dialog
                                    // For brevity implement a simple flow using viewModel.updateEmployee after a dialog
                                })
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "Remover", color = Color.Red, modifier = Modifier.clickable {
                                    viewModel.removeEmployee(index)
                                })
                            }
                        }
                    }
                }
            }

            // Add employee button when editable
            if (isEditable) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            // show dialog to add employee
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Adicionar Funcionário")
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}
