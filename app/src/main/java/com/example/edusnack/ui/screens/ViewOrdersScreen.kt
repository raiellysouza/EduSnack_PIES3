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
import androidx.compose.ui.zIndex
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
import androidx.compose.foundation.indication
import androidx.compose.ui.draw.shadow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewOrdersScreen(nav: NavController, vm: PedidoViewModel = viewModel()) {

    // 0: Hoje, 1: Semana, 2: Todos
    var selectedTimeTab by remember { mutableIntStateOf(0) }
    val timeTabs = listOf("Hoje", "Semana", "Todos")

    // 0: Pendentes, 1: Preparando, 2: Prontos, 3: Entregues
    var selectedStatusTab by remember { mutableIntStateOf(0) }
    val statusTabs = listOf("Pendentes", "Preparando", "Prontos", "Entregues")

    var searchText by remember { mutableStateOf("") }
    val query = searchText.trim().lowercase()
    val tokens = remember(query) { query.split(Regex("\\s+")).filter { it.isNotBlank() } }

    val ordersBySeries by vm.ordersBySeries.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    val expandedMap = remember { mutableStateMapOf<String, Boolean>() }

    fun statusSelecionado(): StatusPedido = when (selectedStatusTab) {
        0 -> StatusPedido.PENDENTE
        1 -> StatusPedido.PREPARANDO
        2 -> StatusPedido.PRONTO
        else -> StatusPedido.ENTREGUE
    }

    fun timeFilterOK(p: Pedido): Boolean {
        if (selectedTimeTab == 2) return true // Todos

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return if (selectedTimeTab == 0) {
            // Hoje
            val start = Timestamp(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 1)
            val end = Timestamp(cal.time)
            p.data >= start && p.data < end
        } else {
            // Semana (segunda -> domingo)
            cal.firstDayOfWeek = Calendar.MONDAY
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            val start = Timestamp(cal.time)
            cal.add(Calendar.DAY_OF_MONTH, 7)
            val end = Timestamp(cal.time)
            p.data >= start && p.data < end
        }
    }

    // SnackBar pro erro
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(error) {
        if (!error.isNullOrBlank()) {
            snackbarHostState.showSnackbar(error!!)
            vm.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Pedidos", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { CanteenBottomNavBar(nav) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
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
                            color = if (selectedTimeTab == index) DarkText else Color.Gray,
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                statusTabs.forEachIndexed { idx, label ->
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
                    placeholder = { Text("Buscar aluno", color = Color(0xFF4CAF50)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF4CAF50)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE9F2E8),
                        unfocusedContainerColor = Color(0xFFE9F2E8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
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
                                            onSetPreparando = { vm.updateStatus(pedido.id, StatusPedido.PREPARANDO) },
                                            onSetPronto = { vm.updateStatus(pedido.id, StatusPedido.PRONTO) },
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
            .background(Color(0xFFF8FFF8))
            .clickable { onToggle() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = DarkText)
            Text("$count pedido(s)", fontSize = 12.sp, color = Color.Gray)
        }
        Icon(
            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = Color.Gray
        )
    }
}

@Composable
private fun PedidoCardPrototype(
    pedido: Pedido,
    onSetPreparando: () -> Unit,
    onSetPronto: () -> Unit,
    onSetEntregue: () -> Unit,
    onCancelar: () -> Unit
) {
    var showConfirmCancel by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF8FFF8), RoundedCornerShape(12.dp))
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
                    color = DarkText
                )

                val itemsText = pedido.itens.joinToString(", ") { it.nome }
                if (itemsText.isNotBlank()) {
                    Text(itemsText, color = Color(0xFF2E7D32), fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Código: ${pedido.codigoRetirada}",
                    color = Color.Gray,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "R$ ${String.format("%.2f", pedido.total)}",
                fontWeight = FontWeight.Bold,
                color = DarkText
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Chips com base + sobreposição do selecionado (protótipo)
        StatusChipsPrototype(
            status = pedido.status,
            onPreparando = onSetPreparando,
            onPronto = onSetPronto
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Botões iguais (mesma altura/largura)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ActionButtonPrototype(
                text = "Marcar como entregue",
                onClick = { onSetEntregue() },
                modifier = Modifier.weight(1f)
            )
            ActionButtonPrototype(
                text = "Cancelar pedido",
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
                ) { Text("Cancelar pedido") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmCancel = false }) { Text("Voltar") }
            }
        )
    }
}

@Composable
private fun StatusChipsPrototype(
    status: StatusPedido,
    onPreparando: () -> Unit,
    onPronto: () -> Unit
) {
    val podeClicarPreparando =
        status == StatusPedido.PENDENTE || status == StatusPedido.PREPARANDO
    val podeClicarPronto =
        status == StatusPedido.PREPARANDO || status == StatusPedido.PRONTO

    val groupBg = Color(0xFFE8F2EB)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(999.dp))   // pill 100% redondo
            .background(groupBg)
            .padding(4.dp)                      // margem interna igual “moldura”
    ) {
        StatusChipPrototype(
            label = "Preparando",
            selected = status == StatusPedido.PREPARANDO,
            enabled = podeClicarPreparando,
            shape = RoundedCornerShape(
                topStart = 999.dp, bottomStart = 999.dp,
                topEnd = 0.dp, bottomEnd = 0.dp
            ),
            modifier = Modifier.weight(1f),
            onClick = onPreparando
        )

        // divisor do meio
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp)
                .background(Color(0xFFD6E6D6))
        )

        StatusChipPrototype(
            label = "Pronto",
            selected = status == StatusPedido.PRONTO,
            enabled = podeClicarPronto,
            shape = RoundedCornerShape(
                topStart = 0.dp, bottomStart = 0.dp,
                topEnd = 999.dp, bottomEnd = 999.dp
            ),
            modifier = Modifier.weight(1f),
            onClick = onPronto
        )
    }
}


@Composable
private fun StatusChipPrototype(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    shape: RoundedCornerShape,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()

    // (Android não tem hover real; se você estiver no Desktop, dá pra adicionar hover depois)
    val showHoverBg = enabled && !selected && pressed

    val greenText = Color(0xFF2F9E44)
    val textColor = when {
        !enabled -> Color(0xFFBDBDBD)
        selected -> Color(0xFF1E1E1E)
        else -> greenText
    }

    val bg = when {
        selected -> Color(0xFFF7FCFA)
        showHoverBg -> Color(0xFFDDE8DD) // cinza/verde claro no “passar/clicar”
        else -> Color.Transparent
    }

    val elevation = when {
        !enabled -> 0.dp
        selected && pressed -> 1.dp
        selected -> 2.dp
        pressed -> 0.2.dp
        else -> 0.dp
    }


    Surface(
        color = bg,
        shape = shape,
        shadowElevation = elevation,
        border = if (selected) BorderStroke(1.dp, Color(0xFFE6E6E6)) else null,
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                enabled = enabled,
                interactionSource = interaction,
                indication = LocalIndication.current
            ) { onClick() }
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = label,
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
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
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5EFE5)),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = text,
            color = DarkText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            lineHeight = 16.sp
        )
    }
}
