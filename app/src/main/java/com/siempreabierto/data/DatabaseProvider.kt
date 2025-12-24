package com.siempreabierto.data

import android.content.Context
import com.siempreabierto.data.dao.*

/**
 * Proveedor de base de datos (Singleton)
 * Facilita el acceso a la BD desde cualquier parte de la app.
 */
object DatabaseProvider {
    
    private var database: AppDatabase? = null
    
    /**
     * Inicializar la base de datos (Llamar en Application.onCreate)
     */
    fun init(context: Context) {
        if (database == null) {
            database = AppDatabase.getInstance(context)
        }
    }
    
    /**
     * Obtener la instancia de la base de datos
     */
    fun getDatabase(): AppDatabase {
        return database ?: throw IllegalStateException(
            "Base de datos no inicializada. Llama a DatabaseProvider.init(context) primero."
        )
    }
    
    // --- Accesos directos a los DAOs ---
    
    fun placeDao(): PlaceDao = getDatabase().placeDao()
    fun restrictionDao(): RestrictionDao = getDatabase().restrictionDao()
    fun contributionDao(): ContributionDao = getDatabase().contributionDao()
    fun helperDao(): HelperDao = getDatabase().helperDao()
    fun routeDao(): RouteDao = getDatabase().routeDao()
    fun userSettingsDao(): UserSettingsDao = getDatabase().userSettingsDao()
}
