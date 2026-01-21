package com.example.edusnack.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.edusnack.data.AuthRepository
import com.example.edusnack.model.Aluno
import com.example.edusnack.model.Pedido
import com.example.edusnack.ui.components.ParentBottomNavBar
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.viewmodel.ParentViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentAccountScreen(nav: NavController, vm: ParentViewModel = viewModel()) {
    val user by vm.user.collectAsState()
    val children by vm.children.collectAsState()
    val recentOrders by vm.recentOrders.collectAsState()
    val loading by vm.loading.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var uploading by remember { mutableStateOf(false) }
    var overflowExpanded by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                uploading = true
                try {
                    val uid = user?.id ?: return@launch
                    val ref = FirebaseStorage.getInstance().reference.child("perfis/$uid.jpg")
                    ref.putFile(it).await()
                    val url = ref.downloadUrl.await().toString()
                    FirebaseFirestore.getInstance().collection("usuarios").document(uid).update("fotoUrl", url).await()
                    vm.loadData() 
                    Toast.makeText(context, "Foto atualizada!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Erro ao subir foto: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    uploading = false
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Conta do Responsável",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { overflowExpanded = true }) {
                        Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Mais", tint = MaterialTheme.colorScheme.onSurface)
                    }

                    DropdownMenu(
                        expanded = overflowExpanded,
                        onDismissRequest = { overflowExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Configurações") },
                            onClick = {
                                overflowExpanded = false
                                nav.navigate("settings")
                            },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Notificações") },
                            onClick = { overflowExpanded = false },
                            leadingIcon = { Icon(Icons.Default.Notifications, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                overflowExpanded = false
                                AuthRepository().logout()
                                nav.navigate("login") { popUpTo("welcome") { inclusive = true } }
                            },
                            leadingIcon = { Icon(Icons.Default.Logout, contentDescription = null) }
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = { ParentBottomNavBar(nav) } // CORREÇÃO: Usando a barra do responsável
    ) { padding ->
        if (loading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GreenPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // --- Seção do Perfil do Pai/Mãe ---
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uploading) {
                            CircularProgressIndicator(color = GreenPrimary)
                        } else {
                            AsyncImage(
                                model = user?.fotoUrl ?: "https://placehold.co/200x200/png?text=${user?.nome?.take(1) ?: "R"}",
                                contentDescription = "Foto do Responsável",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Trocar foto",
                                    tint = Color.White.copy(alpha = 0.8f),
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = user?.nome ?: "Carregando...",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }

                // --- Seção: Meus Dependentes ---
                item {
                    ParentSectionTitle(text = "Meus Dependentes")
                }

                if (children.isEmpty()) {
                    item {
                        Text("Nenhum dependente encontrado.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    items(children) { child ->
                        ChildDependentRow(child)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                // --- Seção: Histórico de Transações ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    ParentSectionTitle(text = "Histórico de Transações")
                }

                if (recentOrders.isEmpty()) {
                    item {
                        Text("Nenhuma transação recente.", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    items(recentOrders) { order ->
                        TransactionSummaryRow(order)
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ParentSectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        textAlign = TextAlign.Start
    )
}

@Composable
fun ChildDependentRow(child: Aluno) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFE0B2)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Foto de ${child.nomeCompleto}",
                modifier = Modifier.size(40.dp),
                tint = Color(0xFF795548)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = child.nomeCompleto,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Turma: ${child.anoOuTurma}",
                fontSize = 14.sp,
                color = Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
fun TransactionSummaryRow(order: Pedido) {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateStr = sdf.format(order.data.toDate())

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = order.itens.joinToString(", ") { it.nome },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = order.alunoNome,
                fontSize = 14.sp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Text(
                text = dateStr,
                fontSize = 12.sp,
                color = Color(0xFF4CAF50)
            )
        }
        Text(
            text = "R$ ${String.format("%.2f", order.total)}",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ParentAccountScreenPreview() {
    ParentAccountScreen(nav = rememberNavController())
}
