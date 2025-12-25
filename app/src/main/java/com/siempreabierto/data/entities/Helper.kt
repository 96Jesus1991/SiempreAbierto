package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Helper - Usuarios que ofrecen ayuda voluntaria
 */
@Entity(tableName = "helpers")
data class Helper(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Identificación (anónima)
    val nickname: String,              // CORREGIDO (antes decía odname)
    val localUserId: String,           // ID local del dispositivo
    
    // Ubicación aproximada
    val latitude: Double,
    val longitude: Double,
    val city: String? = null,
    val province: String? = null,
    val region: String,
    val coverageRadiusKm: Int = 20,
    
    // Contacto
    val phone: String? = null,
    val phoneVisible: Boolean = false,
    val contactNotes: String? = null,
    
    // Tipo de vehículo y capacidades
    val vehicleType: String,
    val canHelpWith: String,          // Lista separada por comas
    val hasTools: Boolean = false,
    val hasJumpCables: Boolean = false,
    val hasTowRope: Boolean = false,
    val hasCompressor: Boolean = false,
    
    // Disponibilidad
    val isAvailable: Boolean = true,
    val availableSchedule: String? = null,
    val availableNotes: String? = null,
    
    // Estadísticas
    val helpCount: Int = 0,
    val rating: Float? = null,
    val ratingCount: Int = 0,
    
    // Trazabilidad
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastActiveAt: Long = System.currentTimeMillis(),
    
    // Estado
    val isVerified: Boolean = false,
    val isActive: Boolean = true
)

/**
 * Tipos de vehículo de ayudantes
 */
object HelperVehicleTypes {
    const val CAR = "car"
    const val VAN = "van"
    const val TRUCK = "truck"
    const val BUS = "bus"
    const val MOTORCYCLE = "motorcycle"
}

/**
 * Extensión para obtener tipos de ayuda como lista
 */
fun Helper.getHelpTypesList(): List<String> {
    return canHelpWith.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}

/**
 * Extensión para verificar si puede ayudar con un tipo específico
 */
fun Helper.canHelpWithType(type: String): Boolean {
    return getHelpTypesList().contains(type)
}
