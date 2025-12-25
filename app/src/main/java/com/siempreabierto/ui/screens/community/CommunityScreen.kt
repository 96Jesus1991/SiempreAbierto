package com.siempreabierto.ui.screens.community

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.siempreabierto.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navController: NavController,
    viewModel: CommunityViewModel = viewModel() // Inyectamos ViewModel
) {
    // 1. Conectamos con el cerebro (ViewModel)
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Estados para diálogos
    var showAddPlaceDialog by remember { mutableStateOf(false) }
    var showHelpOthersDialog by remember { mutableStateOf(false) }

    // Efectos para mensajes (Toasts)
    LaunchedEffect(state.lastActionMessage) {
        state.lastActionMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessage()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Comunidad") })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddPlaceDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, null)
                Spacer(Modifier.width(8.dp))
                Text("Añadir lugar")
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { CommunityWelcomeCard() }

                // Tarjeta de estadísticas (AHORA CONECTADA)
                item {
                    Text("Mis contribuciones", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    ContributionsCard(
                        places = state.placesCreated,
                        confirmations = state.confirmations,
                        total = state.totalContributions
                    )
                }

                // Acciones
                item {
                    Text("Colaborar", style = MaterialTheme.typography.titleLarge)
                }
                
                items(communityActions) { action ->
                    CommunityActionCard(action) {
                        when (action.id) {
                            "add_place" -> showAddPlaceDialog = true
                            "help_others" -> showHelpOthersDialog = true
                            else -> Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(72.dp)) }
            }
        }
    }
    
    if (showAddPlaceDialog) {
        AddPlaceDialog(
            onDismiss = { showAddPlaceDialog = false },
            onTypeSelected = { type -> 
                viewModel.addPlaceMock(type) // Conectado
                showAddPlaceDialog = false
            }
        )
    }
    
    if (showHelpOthersDialog) {
        HelpOthersDialog(
            isHelper = state.isHelper,
            onDismiss = { showHelpOthersDialog = false },
            onConfirm = { 
                viewModel.toggleHelperMode(!state.isHelper) // Conectado (toggle)
                showHelpOthersDialog = false
            }
        )
    }
}

// --- Componentes UI ---

@Composable
fun ContributionsCard(places: Int, confirmations: Int, total: Int) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            ContributionStat(value = places.toString(), label = "Lugares\ncreados")
            ContributionStat(value = confirmations.toString(), label = "Confirmaciones")
            ContributionStat(value = total.toString(), label = "Total\naportes")
        }
    }
}

@Composable
fun ContributionStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium, color = Primary)
        Text(label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
    }
}

@Composable
fun AddPlaceDialog(onDismiss: () -> Unit, onTypeSelected: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Qué quieres añadir?") },
        text = {
            Column {
                listOf("Taller", "Gasolinera", "Parking", "Restaurante").forEach { type ->
                    TextButton(
                        onClick = { onTypeSelected(type) },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text(type) }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
fun HelpOthersDialog(isHelper: Boolean, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isHelper) "Desactivar modo ayudante" else "Quiero ayudar") },
        text = { 
            Text(if (isHelper) 
                "¿Dejar de estar visible para ayudar a otros?" 
                else "Al activar esto, otros conductores podrán ver tu ubicación aproximada si necesitan ayuda urgente.") 
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(if (isHelper) "Desactivar" else "Activar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

// (Mantén tus clases auxiliares CommunityWelcomeCard, CommunityActionCard y la lista communityActions igual que antes, estaban bien visualmente)
@Composable
fun CommunityWelcomeCard() {
    Card(colors = CardDefaults.cardColors(containerColor = Primary.copy(alpha = 0.1f))) {
        Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.People, null, tint = Primary, modifier = Modifier.size(48.dp))
            Text("Comunidad Siempre Abierto", style = MaterialTheme.typography.titleMedium, color = Primary)
            Text("Ayúdanos a mantener el mapa actualizado", style = MaterialTheme.typography.bodySmall)
        }
    }
}

data class CommunityAction(val id: String, val title: String, val icon: ImageVector, val color: androidx.compose.ui.graphics.Color)
val communityActions = listOf(
    CommunityAction("add_place", "Añadir lugar", Icons.Filled.AddLocation, Success),
    CommunityAction("help_others", "Ser ayudante", Icons.Filled.Handshake, CommunityHelper)
)

@Composable
fun CommunityActionCard(action: CommunityAction, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(action.icon, null, tint = action.color)
            Spacer(Modifier.width(16.dp))
            Text(action.title, style = MaterialTheme.typography.titleMedium)
        }
    }
}
