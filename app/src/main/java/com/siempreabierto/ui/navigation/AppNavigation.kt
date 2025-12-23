package com.siempreabierto.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.siempreabierto.ui.screens.home.HomeScreen
import com.siempreabierto.ui.screens.map.MapScreen
import com.siempreabierto.ui.screens.emergency.EmergencyScreen
import com.siempreabierto.ui.screens.community.CommunityScreen
import com.siempreabierto.ui.screens.settings.SettingsScreen

/**
 * Rutas de navegación
 */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : Screen("home", "Inicio", Icons.Filled.Home)
    object Map : Screen("map", "Mapa", Icons.Filled.LocationOn)
    object Emergency : Screen("emergency", "Emergencia", Icons.Filled.Warning)
    object Community : Screen("community", "Comunidad", Icons.Filled.People)
    object Settings : Screen("settings", "Ajustes", Icons.Filled.Settings)
}

// Lista de pantallas para la barra de navegación
val bottomNavItems = listOf(
    Screen.Home,
    Screen.Map,
    Screen.Emergency,
    Screen.Community,
    Screen.Settings
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = screen.icon, 
                                contentDescription = screen.title
                            ) 
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { 
                            it.route == screen.route 
                        } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Evitar múltiples copias en el back stack
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Evitar múltiples copias del mismo destino
                                launchSingleTop = true
                                // Restaurar estado al volver
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { 
                HomeScreen(navController = navController) 
            }
            composable(Screen.Map.route) { 
                MapScreen(navController = navController) 
            }
            composable(Screen.Emergency.route) { 
                EmergencyScreen(navController = navController) 
            }
            composable(Screen.Community.route) { 
                CommunityScreen(navController = navController) 
            }
            composable(Screen.Settings.route) { 
                SettingsScreen(navController = navController) 
            }
        }
    }
}
