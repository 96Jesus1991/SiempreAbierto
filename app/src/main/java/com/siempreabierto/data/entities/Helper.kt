package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Helper - Usuarios que ofrecen ayuda voluntaria
 * 
 * Miembros de la comunidad disponibles para asistir a otros conductores.
 * IMPORTANTE: Ayuda voluntaria, sin pagos obligatorios ni garantías.
 */
@Entity(tableName = "helpers")
data class Helper(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Identificación (anónima)
    val odname: String,              // Apodo o nombre público
    val localUserId: String,          // ID local del dispositivo
    
    // Ubicación aproximada (no exacta por privacidad)
    val latitude: Double,
    val longitude: Double,
    val city: String? = null,
    val province: String? = null,
    val region: String,
    val coverageRadiusKm: Int = 20,   // Radio de cobertura en km
    
    // Contacto
    val phone: String? = null,
    val phoneVisible: Boolean = false, // Si muestra el teléfono públicamente
    val contactNotes: String? = null,  // "Llamar solo de 8 a 22h"
    
    // Tipo de vehículo y capacidades
    val vehicleType: String,          // car, truck, bus, van
    val canHelpWith: String,          // Lista separada por comas
    val hasTools: Boolean = false,
    val hasJumpCables: Boolean = false,
    val hasTowRope: Boolean = false,
    val hasCompressor: Boolean = false,
    
    // Disponibilidad
    val isAvailable: Boolean = true,
    val availableSchedule: String? = null, // "L-V: 7-22h"
    val availableNotes: String? = null,
    
    // Estadísticas
    val helpCount: Int = 0,           // Veces que ha ayudado
    val rating: Float? = null,        // Valoración media (opcional)
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
 * Tipos de ayuda que pueden ofrecer
 */
object HelpTypes {
    const val JUMP_START = "jump_start"       // Puente de batería
    const val FLAT_TIRE = "flat_tire"         // Ayuda con pinchazo
    const val FUEL = "fuel"                   // Llevar combustible
    const val TOW_SHORT = "tow_short"         // Remolque corto
    const val TOOLS = "tools"                 // Prestar herramientas
    const val COMPANY = "company"             // Hacer compañía/esperar
    const val DIRECTIONS = "directions"       // Guiar por la zona
    const val CALL_HELP = "call_help"         // Llamar a grúa/taller
    const val TRANSPORT = "transport"         // Llevar a algún sitio
    
    val ALL = listOf(
        JUMP_START, FLAT_TIRE, FUEL, TOW_SHORT,
        TOOLS, COMPANY, DIRECTIONS, CALL_HELP, TRANSPORT
    )
    
    fun getDisplayName(type: String): String {
        return when (type) {
            JUMP_START -> "Puente de batería"
            FLAT_TIRE -> "Ayuda con pinchazo"
            FUEL -> "Llevar combustible"
            TOW_SHORT -> "Remolque corto"
            TOOLS -> "Prestar herramientas"
            COMPANY -> "Hacer compañía"
            DIRECTIONS -> "Guiar por la zona"
            CALL_HELP -> "Llamar ayuda profesional"
            TRANSPORT -> "Llevar a algún sitio"
            else -> type
        }
    }
}

/**
 * Solicitud de ayuda
 */
@Entity(tableName = "help_requests")
data class HelpRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Quién pide ayuda
    val requesterId: String,
    val requesterName: String? = null,
    val requesterPhone: String? = null,
    
    // Ubicación
    val latitude: Double,
    val longitude: Double,
    val locationDescription: String? = null, // "A-7 km 234, arcén derecho"
    
    // Problema
    val problemType: String,          // battery, flat_tire, fuel, etc.
    val problemDescription: String? = null,
    val vehicleType: String? = null,
    val vehicleDescription: String? = null, // "Camión rojo MAN"
    
    // Estado
    val status: String = "pending",   // pending, accepted, completed, cancelled
    val helperId: Long? = null,       // ID del helper que aceptó
    val helperName: String? = null,
    
    // Tiempos
    val createdAt: Long = System.currentTimeMillis(),
    val acceptedAt: Long? = null,
    val completedAt: Long? = null,
    
    // Resultado
    val wasHelpful: Boolean? = null,
    val rating: Int? = null,          // 1-5
    val feedback: String? = null
)

/**
 * Estados de solicitud de ayuda
 */
object HelpRequestStatus {
    const val PENDING = "pending"
    const val ACCEPTED = "accepted"
    const val IN_PROGRESS = "in_progress"
    const val COMPLETED = "completed"
    const val CANCELLED = "cancelled"
    const val EXPIRED = "expired"
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
