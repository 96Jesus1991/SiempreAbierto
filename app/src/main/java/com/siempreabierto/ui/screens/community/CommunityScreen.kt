
package com.siempreabierto.ui.screens.community

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.siempreabierto.ui.theme.*

/**
 * Pantalla de Comunidad - Colaboración y contribuciones
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavController) {
    var showAddPlaceDialog by remember { mutableStateOf(false) }
    var showHelpOthersDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comunidad") },
                actions = {
                    IconButton(onClick = { /* TODO: Info */ }) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Información"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddPlaceDialog = true },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Añadir lugar")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Mensaje de bienvenida
            item {
                CommunityWelcomeCard()
            }
            
            // Acciones principales
            item {
                Text(
                    text = "¿Cómo quieres colaborar?",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(communityActions) { action ->
                CommunityActionCard(
                    action = action,
                    onClick = {
                        when (action.id) {
                            "add_place" -> showAddPlaceDialog = true
                            "help_others" -> showHelpOthersDialog = true
                            else -> { /* TODO: Otras acciones */ }
                        }
                    }
                )
            }
            
            // Mis contribuciones
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Mis contribuciones",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            item {
                ContributionsCard()
            }
            
            // Aviso legal
            item {
                LegalNoticeCard()
            }
            
            // Espacio para el FAB
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
    
    // Diálogo añadir lugar
    if (showAddPlaceDialog) {
        AddPlaceDialog(onDismiss = { showAddPlaceDialog = false })
    }
    
    // Diálogo ayudar a otros
    if (showHelpOthersDialog) {
        HelpOthersDialog(onDismiss = { showHelpOthersDialog = false })
    }
}

/**
 * Tarjeta de bienvenida a la comunidad
 */
@Composable
fun CommunityWelcomeCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Primary.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.People,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Colaboremos para mejorar la información y ayudarnos en ruta",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Toda la información es aportada por conductores como tú",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Modelo de acción de comunidad
 */
data class CommunityAction(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val color: androidx.compose.ui.graphics.Color
)

/**
 * Lista de acciones de comunidad
 */
val communityActions = listOf(
    CommunityAction(
        id = "add_place",
        title = "Añadir lugar",
        description = "Comparte un taller, gasolinera, parking o servicio",
        icon = Icons.Filled.AddLocation,
        color = Success
    ),
    CommunityAction(
        id = "confirm_open",
        title = "Confirmar abierto",
        description = "Verifica que un lugar sigue funcionando",
        icon = Icons.Filled.CheckCircle,
        color = CommunityVerified
    ),
    CommunityAction(
        id = "edit_info",
        title = "Corregir información",
        description = "Actualiza horarios, precios o datos incorrectos",
        icon = Icons.Filled.Edit,
        color = Info
    ),
    CommunityAction(
        id = "help_others",
        title = "Quiero ayudar a otros",
        description = "Ofrécete para asistir a conductores en apuros",
        icon = Icons.Filled.Handshake,
        color = CommunityHelper
    ),
    CommunityAction(
        id = "report_error",
        title = "Reportar error",
        description = "Informa de datos incorrectos o lugares cerrados",
        icon = Icons.Filled.Report,
        color = Warning
    )
)

/**
 * Tarjeta de acción de comunidad
 */
@Composable
fun CommunityActionCard(
    action: CommunityAction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = action.color,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = action.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Tarjeta de contribuciones del usuario
 */
@Composable
fun ContributionsCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                ContributionStat(
                    value = "0",
                    label = "Lugares\nañadidos"
                )
                ContributionStat(
                    value = "0",
                    label = "Ediciones\nrealizadas"
                )
                ContributionStat(
                    value = "0",
                    label = "Confirmaciones"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡Empieza a contribuir para aparecer aquí!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Estadística de contribución
 */
@Composable
fun ContributionStat(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = Primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Tarjeta de aviso legal
 */
@Composable
fun LegalNoticeCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Gavel,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Aviso importante",
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "• Información orientativa aportada por la comunidad\n" +
                        "• No garantizamos horarios ni disponibilidad\n" +
                        "• No somos responsables de acuerdos entre usuarios\n" +
                        "• Verifica siempre la información antes de actuar",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Diálogo para añadir lugar
 */
@Composable
fun AddPlaceDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir lugar") },
        text = {
            Column {
                Text(
                    text = "Selecciona el tipo de lugar que quieres añadir:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                val placeTypes = listOf(
                    "Taller mecánico", "Taller 24h", "Grúa",
                    "Gasolinera", "Lavadero", "Parking",
                    "Zona descanso", "Supermercado 24h", "Bar/Cafetería"
                )
                
                placeTypes.forEach { type ->
                    TextButton(
                        onClick = { /* TODO: Abrir formulario */ onDismiss() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(type, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/**
 * Diálogo para ofrecerse a ayudar
 */
@Composable
fun HelpOthersDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quiero ayudar a otros") },
        text = {
            Column {
                Text(
                    text = "Al activar esta opción, otros conductores podrán contactarte en caso de emergencia.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Warning.copy(alpha = 0.1f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            tint = Warning,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "La ayuda es voluntaria. No hay pagos obligatorios ni garantías de servicio.",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { /* TODO: Activar */ onDismiss() }) {
                Text("Activar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
