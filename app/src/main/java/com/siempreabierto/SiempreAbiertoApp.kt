package com.siempreabierto

import android.app.Application
import com.siempreabierto.data.DatabaseProvider
import com.siempreabierto.data.AppDatabase
import com.siempreabierto.data.entities.UserSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Siempre Abierto - Aplicación principal
 * 
 * App 100% OFFLINE para viajeros, camioneros y conductores de autobús.
 * Información colaborativa de la comunidad.
 * 
 * SIN Firebase, SIN analytics, SIN tracking.
 * Privacidad total.
 */
class SiempreAbiertoApp : Application() {

    companion object {
        lateinit var instance: SiempreAbiertoApp
            private set
        
        // Versión de la base de datos local
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "siempreabierto.db"
    }

    // Scope para operaciones en background
    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Inicializar base de datos Room
        initializeDatabase()
    }

    /**
     * Inicializar la base de datos y configuración inicial
     */
    private fun initializeDatabase() {
        // Inicializar el proveedor de base de datos
        DatabaseProvider.init(this)
        
        // Crear configuración de usuario si no existe
        applicationScope.launch {
            try {
                val userSettingsDao = DatabaseProvider.userSettingsDao()
                val existingSettings = userSettingsDao.getSettings()
                
                if (existingSettings == null) {
                    // Crear configuración por defecto
                    val defaultSettings = UserSettings(
                        id = 1,
                        localUserId = generateLocalUserId(),
                        nickname = null,
                        vehicleType = "car",
                        darkMode = true,
                        downloadedRegions = "[]"
                    )
                    userSettingsDao.insert(defaultSettings)
                }
            } catch (e: Exception) {
                // Log error pero no crashear
                e.printStackTrace()
            }
        }
    }

    /**
     * Generar ID local único para el usuario
     * Este ID es anónimo y solo se usa para trazabilidad de contribuciones
     */
    private fun generateLocalUserId(): String {
        return "user_${UUID.randomUUID().toString().take(8)}"
    }

    /**
     * Obtener la base de datos
     */
    fun getDatabase(): AppDatabase {
        return DatabaseProvider.getDatabase()
    }
}
