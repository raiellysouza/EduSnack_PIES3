package com.example.edusnack.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.edusnack.ui.theme.GrayBackground
import com.example.edusnack.ui.theme.GreenPrimary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(nav: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance()

    var fotoUrl by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    // Carregar foto atual
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        val doc = db.collection("usuarios").document(uid).get().await()
        fotoUrl = doc.getString("fotoUrl")
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                loading = true
                try {
                    val uid = auth.currentUser?.uid ?: return@launch
                    val ref = storage.reference.child("perfis/$uid.jpg")
                    ref.putFile(it).await()
                    val url = ref.downloadUrl.await().toString()
                    
                    // Atualizar no Firestore
                    db.collection("usuarios").document(uid).update("fotoUrl", url).await()
                    fotoUrl = url
                    Toast.makeText(context, "Foto atualizada!", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(context, "Erro ao subir foto: ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    loading = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configurações", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color.White)
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)) {

                // --- SEÇÃO DA FOTO ---
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(GrayBackground)
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (loading) {
                            CircularProgressIndicator(color = GreenPrimary)
                        } else {
                            AsyncImage(
                                model = fotoUrl ?: "https://placehold.co/200x200/png?text=FOTO",
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            // Ícone de câmera sobreposto
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(text = "Minha Conta", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                // Opções
                SettingsRow(
                    icon = Icons.Filled.Restaurant,
                    label = "Dados da Cantina/Perfil",
                    onClick = { nav.navigate("canteenInfo") }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsRow(
                    icon = Icons.Filled.AccessTime,
                    label = "Horário de funcionamento",
                    onClick = { nav.navigate("canteenInfo") }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        auth.signOut()
                        nav.navigate("login") { popUpTo("welcome") { inclusive = true } }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text(text = "Sair", color = Color.Black, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = GrayBackground,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.Black)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}
