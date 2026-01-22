package com.example.edusnack.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.edusnack.model.Pedido
import com.example.edusnack.model.StatusPedido
import com.example.edusnack.ui.components.CanteenBottomNavBar
import com.example.edusnack.ui.theme.DarkText
import com.example.edusnack.ui.theme.GreenPrimary
import com.example.edusnack.viewmodel.PedidoViewModel
import com.google.firebase.Timestamp
import java.util.Calendar
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.lazy.LazyRow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(nav: NavController, vm: PedidoViewModel = viewModel()) {

    // 0: Hoje, 1: Semana, 2: Todos
    var selectedTimeTab by remember { mutableIntStateOf(0) }
    val timeTabs = listOf("Hoje", "Semana", "Todos")

    // 0: Pendentes, 1: Preparando, 2: Prontos, 3: Entregues, 4: Cancelados
    var selectedStatusTab by remember { mutableIntStateOf(0) }
    val statusTabs = listOf("Pendentes", "Entregues", "Cancelados")

    var searchText by remember { mutableStateOf("") }
    val query = searchText.trim().lowercase()
    val tokens = remember(query) { query.split(Regex("\\s+")).filter { it.isNotBlank() } }

    val ordersBySeries by vm.ordersBySeries.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }

    fun statusSelecionado(): StatusPedido = when (selectedStatusTab) {
        0 -> StatusPedido.PENDENTE
        3 -> StatusPedido.ENTREGUE
        else -> StatusPedido.CANCELADO
    }

    fun timeFilterOK(p: Pedido): Boolean {
        if (selectedTimeTab == 2) return true

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return if (selectedTimeTab == 0) {
            val start = Timestamp(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val end = Timestamp(cal.time)
            p.data >= start && p.data < end
        } else {
            cal.firstDayOfWeek = Calendar.MONDAY
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val start = Timestamp(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 7)
            val end = Timestamp(cal.time)
            p.data >= start && p.data < end
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        if (!error.isNullOrBlank()) {
            snackbarHostState.showSnackbar(error!!)
            // vm.clearError() // Chamada removida se não existir
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pedidos", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = { CanteenBottomNavBar(nav) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            // --- ABAS HOJE / SEMANA / TODOS ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                timeTabs.forEachIndexed { index, title ->
                    Column(
                        modifier = Modifier
                            .padding(end = 24.dp)
                            .clickable { selectedTimeTab = index },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            color = if (selectedTimeTab == index) MaterialTheme.colorScheme.onSurface else Color.Gray,
                            fontWeight = if (selectedTimeTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                        if (selectedTimeTab == index) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(3.dp)
                                    .background(GreenPrimary, RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ABAS DE STATUS ---
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(statusTabs.size) { idx ->
                    val label = statusTabs[idx]
                    val isSelected = idx == selectedStatusTab
                    Text(
                        text = label,
                        color = if (isSelected) GreenPrimary else Color.Gray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { selectedStatusTab = idx }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- BUSCA ---
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                TextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    placeholder = { Text("Buscar aluno", color = MaterialTheme.colorScheme.primary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
                return@Column
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ordersBySeries.forEach { (series, pedidosRaw) ->

                    val pedidos = pedidosRaw
                        .asSequence()
                        .filter { it.status == statusSelecionado() }
                        .filter { timeFilterOK(it) }
                        .filter { p ->
                            if (tokens.isEmpty()) true
                            else tokens.all { tok -> p.alunoNome.lowercase().contains(tok) }
                        }
                        .toList()

                    if (pedidos.isNotEmpty()) {
                        val expanded = expandedMap[series] ?: tokens.isNotEmpty()

                        item {
                            SeriesHeader(
                                title = series,
                                count = pedidos.size,
                                expanded = expanded,
                                onToggle = { expandedMap[series] = !expanded }
                            )
                        }

                        item {
                            AnimatedVisibility(visible = expanded) {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    pedidos.forEach { pedido ->
                                        PedidoCardPrototype(
                                            pedido = pedido,
                                            onSetEntregue = { vm.markAsDelivered(pedido.id) },
                                            onCancelar = { vm.cancelOrder(pedido.id) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun SeriesHeader(title: String, count: Int, expanded: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            Text("$count pedido(s)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PedidoCardPrototype(
    pedido: Pedido,
    onSetEntregue: () -> Unit,
    onCancelar: () -> Unit
) {
    var showConfirmCancel by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                val titulo = listOf(pedido.alunoNome, pedido.turma)
                    .filter { it.isNotBlank() }
                    .joinToString(" / ")

                Text(
                    text = if (titulo.isNotBlank()) titulo else "Aluno",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val itemsText = pedido.itens.joinToString(", ") { it.nome }
                if (itemsText.isNotBlank()) {
                    Text(itemsText, color = MaterialTheme.colorScheme.primary, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Código: ${pedido.codigoRetirada}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "R$ ${String.format("%.2f", pedido.total)}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))


        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ActionButtonPrototype(
                text = "Entregue hoje",
                onClick = { onSetEntregue() },
                modifier = Modifier.weight(1f)
            )
            ActionButtonPrototype(
                text = "Cancelar",
                onClick = { showConfirmCancel = true },
                modifier = Modifier.weight(1f)
            )
        }
    }

    if (showConfirmCancel) {
        AlertDialog(
            onDismissRequest = { showConfirmCancel = false },
            title = { Text("Cancelar pedido") },
            text = { Text("Tem certeza que deseja cancelar este pedido?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmCancel = false
                        onCancelar()
                    }
                ) { Text("Sim, cancelar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmCancel = false }) { Text("Voltar") }
            }
        )
    }
}


@Composable
private fun ActionButtonPrototype(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(text = text, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}