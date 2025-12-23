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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.siempreabierto.ui.theme.*

/**
 * Pantalla de Ajustes - Configuración y privacidad
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    var darkMode by remember { mutableStateOf(true) }
    var showLegalDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajustes") }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Sección: Apariencia
            item {
                SettingsSectionTitle("Apariencia")
            }
            
            item {
                SettingsToggleItem(
                    icon = Icons.Filled.DarkMode,
                    title = "Modo oscuro",
                    subtitle = "Mejor para conducción nocturna",
                    checked = darkMode,
                    onCheckedChange = { darkMode = it }
                )
            }
            
            // Sección: Mapas
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionTitle("Mapas offline")
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Filled.Map,
                    title = "Gestionar mapas",
                    subtitle = "Descargar o eliminar regiones",
                    onClick = { /* TODO: Pantalla mapas */ }
                )
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Filled.Storage,
                    title = "Almacenamiento",
                    subtitle = "Mapas: 0 MB • Datos: 0 MB",
                    onClick = { /* TODO: Detalles almacenamiento */ }
                )
            }
            
            // Sección: Datos
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionTitle("Datos y sincronización")
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Filled.Sync,
                    title = "Sincronizar datos",
                    subtitle = "Última sync: Nunca",
                    onClick = { /* TODO: Sincronizar */ }
                )
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Filled.DeleteForever,
                    title = "Borrar datos locales",
                    subtitle = "Eliminar lugares y contribuciones guardadas",
                    onClick = { /* TODO: Confirmar borrado */ }
                )
            }
            
            // Sección: Privacidad
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionTitle("Privacidad")
            }
            
            item {
                PrivacyInfoCard()
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Filled.PrivacyTip,
                    title = "Política de privacidad",
                    subtitle = "Cómo protegemos tus datos",
                    onClick = { showPrivacyDialog = true }
                )
            }
            
            // Sección: Legal
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionTitle("Legal")
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Filled.Gavel,
                    title = "Aviso legal",
                    subtitle = "Términos y condiciones de uso",
                    onClick = { showLegalDialog = true }
                )
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Filled.Description,
                    title = "Licencias",
                    subtitle = "OpenStreetMap y código abierto",
                    onClick = { /* TODO: Pantalla licencias */ }
                )
            }
            
            // Sección: Acerca de
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsSectionTitle("Acerca de")
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Filled.Info,
                    title = "Siempre Abierto",
                    subtitle = "Versión 1.0.0",
                    onClick = { showAboutDialog = true }
                )
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Filled.Star,
                    title = "Valorar en Play Store",
                    subtitle = "¡Tu opinión nos ayuda!",
                    onClick = { /* TODO: Abrir Play Store */ }
                )
            }
            
            item {
                SettingsClickItem(
                    icon = Icons.Filled.Email,
                    title = "Contacto",
                    subtitle = "Reportar problemas o sugerencias",
                    onClick = { /* TODO: Email */ }
                )
            }
            
            // Espacio final
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
    
    // Diálogos
    if (showLegalDialog) {
        LegalDialog(onDismiss = { showLegalDialog = false })
    }
    
    if (showPrivacyDialog) {
        PrivacyDialog(onDismiss = { showPrivacyDialog = false })
    }
    
    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }
}

/**
 * Título de sección
 */
@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

/**
 * Item de ajuste clickeable
 */
@Composable
fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
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
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
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
 * Item de ajuste con toggle
 */
@Composable
fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
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
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

/**
 * Tarjeta de información de privacidad
 */
@Composable
fun PrivacyInfoCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Success.copy(alpha = 0.1f)
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
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    tint = Success,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Privacidad Total",
                    style = MaterialTheme.typography.titleMedium,
                    color = Success
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            PrivacyFeature(Icons.Filled.PersonOff, "Sin login obligatorio")
            PrivacyFeature(Icons.Filled.EmailOff, "Sin email requerido")
            PrivacyFeature(Icons.Filled.BlockFlipped, "Sin anuncios")
            PrivacyFeature(Icons.Filled.VisibilityOff, "Sin tracking")
            PrivacyFeature(Icons.Filled.PhoneAndroid, "Datos locales en tu dispositivo")
        }
    }
}

/**
 * Feature de privacidad
 */
@Composable
fun PrivacyFeature(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

/**
 * Diálogo de aviso legal
 */
@Composable
fun LegalDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Aviso Legal") },
        text = {
            Column {
                Text(
                    text = "INFORMACIÓN IMPORTANTE",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "• Toda la información mostrada es ORIENTATIVA y ha sido aportada por la comunidad de usuarios.\n\n" +
                            "• NO garantizamos la exactitud de horarios, precios, disponibilidad o cualquier otro dato.\n\n" +
                            "• NO somos responsables de acuerdos, transacciones o interacciones entre usuarios.\n\n" +
                            "• Verifica SIEMPRE la señalización vial oficial para restricciones de altura, peso y anchura.\n\n" +
                            "• Esta app es una plataforma de contacto comunitario. NO garantizamos asistencia en emergencias.\n\n" +
                            "• Los mapas están basados en OpenStreetMap y pueden contener inexactitudes.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido")
            }
        }
    )
}

/**
 * Diálogo de privacidad
 */
@Composable
fun PrivacyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Política de Privacidad") },
        text = {
            Column {
                Text(
                    text = "TU PRIVACIDAD ES NUESTRA PRIORIDAD",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "• NO recopilamos datos personales.\n\n" +
                            "• NO requerimos registro ni email.\n\n" +
                            "• NO usamos cookies de seguimiento.\n\n" +
                            "• NO mostramos publicidad.\n\n" +
                            "• NO vendemos ni compartimos información.\n\n" +
                            "• Todos los datos se guardan LOCALMENTE en tu dispositivo.\n\n" +
                            "• La sincronización con la comunidad es opcional y anónima.",
                    style = MaterialTheme.typography.bodySmall
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

/**
 * Diálogo Acerca de
 */
@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Siempre Abierto") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.LocalShipping,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Comunidad en Ruta - OFFLINE",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Versión 1.0.0",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "App creada para viajeros, camioneros y conductores de autobús.\n\n" +
                            "100% funcional sin internet.\n" +
                            "Información colaborativa de la comunidad.\n" +
                            "Sin anuncios. Sin suscripciones.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Mapas © OpenStreetMap contributors",
                    style = MaterialTheme.typography.labelSmall,
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
