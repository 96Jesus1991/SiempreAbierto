package com.siempreabierto

import android.app.Application

/**
 * Siempre Abierto - Aplicación principal
 * 
 * App 100% OFFLINE para viajeros, camioneros y conductores de autobús.
 * Información colaborativa de la comunidad.
 */
class SiempreAbiertoApp : Application() {

    companion object {
        lateinit var instance: SiempreAbiertoApp
            private set
        
        // Versión de la base de datos local
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "siempreabierto.db"
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        
        // Inicializar base de datos Room (se hace lazy en DatabaseProvider)
        // No se usa Firebase ni analytics - Privacidad total
    }
}
