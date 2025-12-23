package com.siempreabierto.ui.screens.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.siempreabierto.ui.theme.*

/**
 * Pantalla de Mapa - Visualización offline con OpenStreetMap
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(navController: NavController) {
    var selectedVehicle by remember { mutableStateOf(VehicleType.CAR) }
    var showDownloadDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa Offline") },
                actions = {
                    // Botón para descargar mapas
                    IconButton(onClick = { showDownloadDialog = true }) {
                        Icon(
                            imageVector = Icons.Filled.Download,
                            contentDescription = "Descargar mapas"
                        )
                    }
                    // Botón para centrar en ubicación
                    IconButton(onClick = { /* TODO: Centrar GPS */ }) {
                        Icon(
                            imageVector = Icons.Filled.MyLocation,
                            contentDescription = "Mi ubicación"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Área del mapa (placeholder - aquí irá MapLibre/Mapsforge)
            MapPlaceholder()
            
            // Filtros de vehículo en la parte superior
            VehicleFilterBar(
                selectedVehicle = selectedVehicle,
                onVehicleSelected = { selectedVehicle = it },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
            
            // Barra de búsqueda
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 80.dp, start = 16.dp, end = 16.dp)
            )
            
            // Controles de zoom
            ZoomControls(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(16.dp)
            )
            
            // Info de modo offline
            OfflineIndicator(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            )
        }
    }
    
    // Diálogo de descarga de mapas
    if (showDownloadDialog) {
        MapDownloadDialog(
            onDismiss = { showDownloadDialog = false }
        )
    }
}

/**
 * Placeholder del mapa (se reemplazará por MapLibre)
 */
@Composable
fun MapPlaceholder() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2D2D2D)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Map,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Mapa OpenStreetMap",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Descarga tu región para usar offline",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Tipos de vehículo
 */
enum class VehicleType(val label: String, val color: Color) {
    CAR("Coche", VehicleCar),
    TRUCK("Camión", VehicleTruck),
    BUS("Autobús", VehicleBus),
    CAMPER("Camper", VehicleCamper)
}

/**
 * Barra de filtro por tipo de vehículo
 */
@Composable
fun VehicleFilterBar(
    selectedVehicle: VehicleType,
    onVehicleSelected: (VehicleType) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        LazyRow(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(VehicleType.values().toList()) { vehicle ->
                FilterChip(
                    selected = selectedVehicle == vehicle,
                    onClick = { onVehicleSelected(vehicle) },
                    label = { Text(vehicle.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = vehicle.color,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

/**
 * Barra de búsqueda local
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
        )
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Buscar lugar...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { searchText = "" }) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Limpiar"
                        )
                    }
                }
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Controles de zoom
 */
@Composable
fun ZoomControls(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            IconButton(onClick = { /* TODO: Zoom in */ }) {
                Icon(Icons.Filled.Add, contentDescription = "Acercar")
            }
            Divider()
            IconButton(onClick = { /* TODO: Zoom out */ }) {
                Icon(Icons.Filled.Remove, contentDescription = "Alejar")
            }
        }
    }
}

/**
 * Indicador de modo offline
 */
@Composable
fun OfflineIndicator(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Success.copy(alpha = 0.9f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.OfflinePin,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Modo offline activo",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White
            )
        }
    }
}

/**
 * Diálogo de descarga de mapas por región
 */
@Composable
fun MapDownloadDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Descargar Mapas") },
        text = {
            Column {
                Text(
                    text = "Selecciona las regiones que quieres descargar para usar sin conexión.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Lista de regiones (simplificada)
                val regions = listOf(
                    "Andalucía", "Aragón", "Asturias", "Baleares",
                    "Canarias", "Cantabria", "Castilla-La Mancha",
                    "Castilla y León", "Cataluña", "Extremadura",
                    "Galicia", "La Rioja", "Madrid", "Murcia",
                    "Navarra", "País Vasco", "C. Valenciana"
                )
                regions.take(5).forEach { region ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(region)
                        TextButton(onClick = { /* TODO: Descargar */ }) {
                            Text("Descargar")
                        }
                    }
                }
                Text(
                    text = "...y más regiones",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
