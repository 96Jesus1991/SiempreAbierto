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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.siempreabierto.ui.theme.*

/**
 * Pantalla de Mapa - CONECTADA
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: MapViewModel = viewModel() // Inyectamos el ViewModel
) {
    // 1. Observamos el estado real del ViewModel
    val state by viewModel.uiState.collectAsState()
    
    // Estado local solo para diálogos UI
    var showDownloadDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa Offline") },
                actions = {
                    IconButton(onClick = { showDownloadDialog = true }) {
                        Icon(Icons.Filled.Download, "Descargar mapas")
                    }
                    IconButton(onClick = { /* TODO: Centrar GPS */ }) {
                        Icon(Icons.Filled.MyLocation, "Mi ubicación", tint = if(state.hasUserLocation) Success else LocalContentColor.current)
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            
            // Placeholder del mapa (Aquí iría la integración real con MapLibre/Mapsforge)
            // Le pasamos los datos reales del estado
            MapPlaceholder(regionCount = state.downloadedRegions.size)
            
            // Barra de Filtros (Conectada)
            VehicleFilterBar(
                selectedVehicle = state.selectedVehicleType,
                onVehicleSelected = { viewModel.setVehicleType(it) }, // Conectado al ViewModel
                modifier = Modifier.align(Alignment.TopCenter).padding(16.dp)
            )
            
            // Barra de Búsqueda (Conectada)
            SearchBar(
                onSearch = { viewModel.searchOnMap(it) }, // Conectado al ViewModel
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 80.dp, start = 16.dp, end = 16.dp)
            )
            
            // Controles de zoom (Conectados)
            ZoomControls(
                onZoomIn = { viewModel.updateZoom(state.currentZoom + 1) },
                onZoomOut = { viewModel.updateZoom(state.currentZoom - 1) },
                modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp)
            )
            
            // Indicador Offline
            OfflineIndicator(modifier = Modifier.align(Alignment.BottomStart).padding(16.dp))
        }
    }
    
    if (showDownloadDialog) {
        MapDownloadDialog(onDismiss = { showDownloadDialog = false })
    }
}

@Composable
fun MapPlaceholder(regionCount: Int) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color(0xFF2D2D2D)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.Map, null, modifier = Modifier.size(64.dp), tint = Primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Mapa OpenStreetMap", style = MaterialTheme.typography.titleMedium)
            
            if (regionCount > 0) {
                Text("✅ $regionCount regiones descargadas", style = MaterialTheme.typography.bodyMedium, color = Success)
            } else {
                Text("Descarga tu región para usar offline", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}

@Composable
fun VehicleFilterBar(
    selectedVehicle: String,
    onVehicleSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Definimos los tipos aquí para que coincidan con UserVehicleTypes
    val vehicles = listOf(
        "car" to "Coche",
        "truck" to "Camión",
        "bus" to "Bus",
        "camper" to "Camper"
    )

    Card(modifier = modifier, shape = RoundedCornerShape(24.dp)) {
        LazyRow(modifier = Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(vehicles) { (id, label) ->
                val isSelected = selectedVehicle == id || (selectedVehicle.startsWith("truck") && id == "truck")
                FilterChip(
                    selected = isSelected,
                    onClick = { onVehicleSelected(id) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = if(id == "truck") VehicleTruck else Primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(onSearch: (String) -> Unit, modifier: Modifier = Modifier) {
    var searchText by remember { mutableStateOf("") }
    
    Card(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(28.dp)) {
        TextField(
            value = searchText,
            onValueChange = { 
                searchText = it
                onSearch(it) 
            },
            placeholder = { Text("Buscar gasolinera, taller...") },
            leadingIcon = { Icon(Icons.Filled.Search, null) },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { searchText = ""; onSearch("") }) {
                        Icon(Icons.Filled.Clear, "Limpiar")
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

@Composable
fun ZoomControls(onZoomIn: () -> Unit, onZoomOut: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp)) {
        Column {
            IconButton(onClick = onZoomIn) { Icon(Icons.Filled.Add, "Acercar") }
            Divider()
            IconButton(onClick = onZoomOut) { Icon(Icons.Filled.Remove, "Alejar") }
        }
    }
}

// (OfflineIndicator y MapDownloadDialog se mantienen igual que en tu archivo original)
@Composable
fun OfflineIndicator(modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Success.copy(alpha = 0.9f))) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.OfflinePin, null, tint = Color.White, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(6.dp))
            Text("Modo offline activo", style = MaterialTheme.typography.labelMedium, color = Color.White)
        }
    }
}

@Composable
fun MapDownloadDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Descargar Mapas") },
        text = { Text("Aquí aparecerá la lista de Comunidades Autónomas para descargar.") },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}
}
