package com.siempreabierto.ui.screens.emergency

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.siempreabierto.ui.theme.*

/**
 * Pantalla de Emergencia - "ME QUEDÉ TIRADO"
 * Diseñada para uso rápido en situaciones de estrés
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(navController: NavController) {
    var selectedProblem by remember { mutableStateOf<EmergencyProblem?>(null) }
    var showResults by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "EMERGENCIA",
                        color = EmergencyRed
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (!showResults) {
            // Paso 1: Seleccionar problema
            ProblemSelectionScreen(
                selectedProblem = selectedProblem,
                onProblemSelected = { selectedProblem = it },
                onContinue = { showResults = true },
                modifier = Modifier.padding(padding)
            )
        } else {
            // Paso 2: Mostrar ayuda cercana
            EmergencyResultsScreen(
                problem = selectedProblem!!,
                onBack = { showResults = false },
                modifier = Modifier.padding(padding)
            )
        }
    }
}

/**
 * Pantalla de selección de problema
 */
@Composable
fun ProblemSelectionScreen(
    selectedProblem: EmergencyProblem?,
    onProblemSelected: (EmergencyProblem) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "¿Qué te ha pasado?",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Selecciona tu problema para mostrarte ayuda cercana",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // Lista de problemas
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(emergencyProblems) { problem ->
                ProblemCard(
                    problem = problem,
                    isSelected = selectedProblem == problem,
                    onClick = { onProblemSelected(problem) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Botón continuar
        Button(
            onClick = onContinue,
            enabled = selectedProblem != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EmergencyRed
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "BUSCAR AYUDA CERCANA",
                style = MaterialTheme.typography.titleMedium
            )
        }
        
        // Aviso legal
        Text(
            text = "Plataforma de contacto comunitario. No se garantiza asistencia.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp)
        )
    }
}

/**
 * Tarjeta de problema
 */
@Composable
fun ProblemCard(
    problem: EmergencyProblem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                EmergencyRed.copy(alpha = 0.2f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = androidx.compose.ui.graphics.SolidColor(EmergencyRed)
            )
        } else null,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = problem.icon,
                contentDescription = null,
                tint = if (isSelected) EmergencyRed else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = problem.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = problem.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = EmergencyRed
                )
            }
        }
    }
}

/**
 * Pantalla de resultados de emergencia
 */
@Composable
fun EmergencyResultsScreen(
    problem: EmergencyProblem,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Problema seleccionado
        Card(
            colors = CardDefaults.cardColors(
                containerColor = EmergencyBackground
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = problem.icon,
                    contentDescription = null,
                    tint = EmergencyRed
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = problem.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                TextButton(onClick = onBack) {
                    Text("Cambiar")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Sección: Talleres/Grúas cercanas
        Text(
            text = "Ayuda profesional cercana",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Placeholder de resultados
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Buscando en tu zona...",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Los resultados se mostrarán aquí",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Sección: Comunidad que puede ayudar
        Text(
            text = "Comunidad disponible",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CommunityHelper.copy(alpha = 0.1f)
            )
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
                        imageVector = Icons.Filled.People,
                        contentDescription = null,
                        tint = CommunityHelper
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Usuarios que pueden ayudar",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Miembros de la comunidad que voluntariamente ofrecen ayuda a otros conductores.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Sin pagos obligatorios • Sin garantías • Ayuda voluntaria",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Aviso importante
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Warning.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = Warning,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Información aportada por la comunidad. Verifica siempre antes de actuar.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Modelo de problema de emergencia
 */
data class EmergencyProblem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

/**
 * Lista de problemas de emergencia
 */
val emergencyProblems = listOf(
    EmergencyProblem(
        id = "battery",
        title = "Batería descargada",
        description = "No arranca, necesito puente o cargador",
        icon = Icons.Filled.BatteryAlert
    ),
    EmergencyProblem(
        id = "flat_tire",
        title = "Pinchazo",
        description = "Rueda pinchada, necesito ayuda",
        icon = Icons.Filled.TireRepair
    ),
    EmergencyProblem(
        id = "wont_start",
        title = "No arranca",
        description = "El motor no enciende",
        icon = Icons.Filled.CarCrash
    ),
    EmergencyProblem(
        id = "overheating",
        title = "Sobrecalentamiento",
        description = "Motor caliente, humo o vapor",
        icon = Icons.Filled.Whatshot
    ),
    EmergencyProblem(
        id = "brakes",
        title = "Problema de frenos",
        description = "Frenos fallan o hacen ruido",
        icon = Icons.Filled.Warning
    ),
    EmergencyProblem(
        id = "fuel",
        title = "Sin combustible",
        description = "Me quedé sin gasolina/diésel",
        icon = Icons.Filled.LocalGasStation
    ),
    EmergencyProblem(
        id = "locked",
        title = "Llaves dentro",
        description = "Me dejé las llaves en el coche",
        icon = Icons.Filled.Key
    ),
    EmergencyProblem(
        id = "other",
        title = "Otro problema",
        description = "Cualquier otra avería o situación",
        icon = Icons.Filled.Help
    )
)
