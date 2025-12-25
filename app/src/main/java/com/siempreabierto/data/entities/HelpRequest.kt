package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "help_requests")
data class HelpRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val localId: String, // ID único para sincronización
    
    val lat: Double,
    val lon: Double,
    val locationDescription: String,
    
    val type: String, // breakdown, fuel, stuck, medical
    val description: String,
    
    val contactInfo: String? = null, // Opcional (teléfono temporal)
    
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long, // Se borra automáticamente en 24h
    
    val status: String = "active", // active, resolved, expired
    val helperCount: Int = 0
)

object HelpTypes {
    const val BREAKDOWN = "breakdown" // Avería mecánica
    const val FUEL = "fuel"           // Sin gasolina
    const val STUCK = "stuck"         // Atascado en barro/nieve
    const val FLAT_TIRE = "flat_tire" // Rueda pinchada
    const val MEDICAL = "medical"     // Emergencia médica leve
    const val OTHER = "other"
}
