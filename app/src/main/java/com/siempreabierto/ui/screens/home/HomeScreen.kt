package com.siempreabierto.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.siempreabierto.ui.navigation.Screen
import com.siempreabierto.ui.theme.*

/**
 * Pantalla de inicio - Acceso rápido a categorías principales
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Siempre Abierto",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "Comunidad en Ruta - OFFLINE",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Botón de emergencia destacado
            EmergencyButton(
                onClick = { navController.navigate(Screen.Emergency.route) }
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Título de categorías
            Text(
                text = "¿Qué necesitas?",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            // Grid de categorías
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { 
                            // TODO: Navegar a la categoría específica
                            navController.navigate(Screen.Map.route)
                        }
                    )
                }
            }
        }
    }
}

/**
 * Botón grande de emergencia
 */
@Composable
fun EmergencyButton(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = EmergencyRed
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ME QUEDÉ TIRADO",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }
    }
}

/**
 * Tarjeta de categoría
 */
@Composable
fun CategoryCard(
    category: Category,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = null,
                tint = category.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelLarge,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

/**
 * Modelo de categoría
 */
data class Category(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

/**
 * Lista de categorías disponibles
 */
val categories = listOf(
    Category("workshops", "Talleres", Icons.Filled.Build, CatWorkshop),
    Category("workshops_24h", "Talleres 24h", Icons.Filled.Brightness2, CatWorkshop),
    Category("tow_trucks", "Grúas", Icons.Filled.LocalShipping, CatWorkshop),
    Category("gas_stations", "Gasolineras", Icons.Filled.LocalGasStation, CatGasStation),
    Category("car_wash", "Lavaderos", Icons.Filled.LocalCarWash, CatWash),
    Category("parking", "Parkings", Icons.Filled.LocalParking, CatParking),
    Category("rest_areas", "Zonas Descanso", Icons.Filled.Hotel, CatRest),
    Category("supermarkets", "Supermercados 24h", Icons.Filled.ShoppingCart, CatFood),
    Category("restaurants", "Bares/Cafeterías", Icons.Filled.Restaurant, CatFood),
    Category("restrictions", "Restricciones", Icons.Filled.Height, Error)
)
