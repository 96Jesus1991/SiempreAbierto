package com.siempreabierto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.siempreabierto.ui.theme.SiempreAbiertoTheme
import com.siempreabierto.ui.navigation.AppNavigation

/**
 * MainActivity - Punto de entrada de la aplicación
 * 
 * Usa Jetpack Compose para toda la UI.
 * Diseñada para uso en carretera: botones grandes, texto legible.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Habilitar edge-to-edge para diseño moderno
        enableEdgeToEdge()
        
        setContent {
            SiempreAbiertoTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
