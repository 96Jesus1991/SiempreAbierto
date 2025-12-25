package com.siempreabierto.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.siempreabierto.ui.theme.*
import android.widget.Toast

/**
 * Pantalla de Ajustes - CONECTADA
 * Ahora guarda los cambios en la base de datos a través del ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel() // Inyectamos el ViewModel automáticamente
) {
    // 1. Observamos el estado real de la base de datos
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    // Estados para diálogos locales
    var showLegalDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Efecto para mostrar mensajes de error o éxito
    LaunchedEffect(state.error) {
        state.error?.let {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    LaunchedEffect(state.dataCleared) {
        if (state.dataCleared) {
            Toast.makeText(context, "Datos borrados correctamente", Toast.LENGTH_SHORT).show()
            viewModel.clearDataCleared()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // --- SECCIÓN APARIENCIA ---
                item { SettingsSectionTitle("Apariencia") }
                
                item {
                    SettingsToggleItem(
                        icon = Icons.Filled.DarkMode,
                        title = "Modo oscuro",
                        subtitle = "Mejor para conducción nocturna",
                        checked = state.darkMode,
                        onCheckedChange = { viewModel.setDarkMode(it) } // Conectado
                    )
                }

                // --- SECCIÓN MAPAS ---
                item { 
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsSectionTitle("Mapas offline") 
                }
                
                item {
                    SettingsToggleItem(
                        icon = Icons.Filled.Warning,
                        title = "Mostrar restricciones",
                        subtitle = "Puentes bajos, límites de peso...",
                        checked = state.showRestrictions,
                        onCheckedChange = { viewModel.setShowRestrictions(it) }
                    )
                }

                item {
                    SettingsToggleItem(
                        icon = Icons.Filled.Handshake,
                        title = "Mostrar ayuda",
                        subtitle = "Ver usuarios que pueden ayudar",
                        checked = state.showHelpers,
                        onCheckedChange = { viewModel.setShowHelpers(it) }
                    )
                }
                
                // --- SECCIÓN VEHÍCULO ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsSectionTitle("Mi Vehículo")
                }

                item {
                    // Selector simple de tipo de vehículo (podría ser un diálogo más complejo)
                    SettingsClickItem(
                        icon = Icons.Filled.LocalShipping,
                        title = "Tipo de vehículo",
                        subtitle = when(state.vehicleType) {
                            "car" -> "Coche"
                            "truck" -> "Camión"
                            "bus" -> "Autobús"
                            "camper" -> "Autocaravana"
                            else -> "Otro"
                        },
                        onClick = { 
                            // Ciclo simple para demo: Coche -> Camión -> Bus -> Coche
                            val nextType = when(state.vehicleType) {
                                "car" -> "truck"
                                "truck" -> "bus"
                                "bus" -> "camper"
                                else -> "car"
                            }
                            viewModel.setVehicleType(nextType)
                            Toast.makeText(context, "Cambiado a: $nextType", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                // --- SECCIÓN DATOS ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsSectionTitle("Datos")
                }
                
                item {
                    SettingsClickItem(
                        icon = Icons.Filled.DeleteForever,
                        title = "Borrar datos locales",
                        subtitle = "Eliminar lugares y rutas guardadas",
                        onClick = { showDeleteConfirm = true }
                    )
                }
                
                // --- SECCIÓN PRIVACIDAD ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsSectionTitle("Privacidad")
                }
                
                item { PrivacyInfoCard() }
                
                item {
                    SettingsClickItem(
                        icon = Icons.Filled.PrivacyTip,
                        title = "Política de privacidad",
                        subtitle = "Cómo protegemos tus datos",
                        onClick = { showPrivacyDialog = true }
                    )
                }
                
                // --- SECCIÓN LEGAL ---
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SettingsSectionTitle("Legal & Info")
                }
                
                item {
                    SettingsClickItem(
                        icon = Icons.Filled.Info,
                        title = "Acerca de",
                        subtitle = "Versión 1.0.0",
                        onClick = { showAboutDialog = true }
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
    
    // --- DIÁLOGOS ---

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("¿Borrar todo?") },
            text = { Text("Esta acción eliminará tus rutas guardadas y configuraciones. No se puede deshacer.") },
            confirmButton = {
                TextButton(
                    onClick = { 
                        viewModel.clearAllData()
                        showDeleteConfirm = false 
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Borrar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            }
        )
    }
    
    if (showLegalDialog) { LegalDialog(onDismiss = { showLegalDialog = false }) }
    if (showPrivacyDialog) { PrivacyDialog(onDismiss = { showPrivacyDialog = false }) }
    if (showAboutDialog) { AboutDialog(onDismiss = { showAboutDialog = false }) }
}

// --- COMPONENTES UI AUXILIARES (Igual que antes) ---

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun SettingsClickItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(Icons.Filled.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SettingsToggleItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
fun PrivacyInfoCard() {
    Card(colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Shield, null, tint = Success, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Privacidad Total", style = MaterialTheme.typography.titleMedium, color = Success)
            }
            Spacer(Modifier.height(12.dp))
            PrivacyFeature(Icons.Filled.PersonOff, "Sin login obligatorio")
            PrivacyFeature(Icons.Filled.BlockFlipped, "Sin anuncios")
            PrivacyFeature(Icons.Filled.PhoneAndroid, "Datos locales en tu dispositivo")
        }
    }
}

@Composable
fun PrivacyFeature(icon: ImageVector, text: String) {
    Row(Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}

// (Mantén los diálogos LegalDialog, PrivacyDialog y AboutDialog igual que en tu archivo original, esos estaban bien)
@Composable
fun LegalDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aviso Legal") },
        text = { Text("Información legal y descargos de responsabilidad...") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Entendido") } }
    )
}

@Composable
fun PrivacyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Política de Privacidad") },
        text = { Text("Tus datos son tuyos. No tracking. No ads.") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Siempre Abierto") },
        text = { Text("Versión 1.0.0\nCreado para la comunidad.") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Genial") } }
    )
}
