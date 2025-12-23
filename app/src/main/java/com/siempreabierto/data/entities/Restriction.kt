package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Restriction - Puntos críticos de restricción
 * 
 * Alturas máximas, anchos limitados, pesos recomendados.
 * IMPORTANTE: Información orientativa, verificar siempre señalización vial.
 */
@Entity(tableName = "restrictions")
data class Restriction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Ubicación del punto crítico
    val latitude: Double,
    val longitude: Double,
    val name: String,                // "Túnel A-7 km 234", "Puente Río Tajo"
    val description: String? = null,
    val roadName: String? = null,    // "A-7", "N-340", "CV-35"
    val kmPoint: String? = null,     // "km 234"
    
    // Tipo de restricción
    val type: String,                // height, width, weight, combined
    
    // Valores de restricción
    val maxHeight: Double? = null,   // metros
    val maxWidth: Double? = null,    // metros
    val maxWeight: Double? = null,   // toneladas
    val maxLength: Double? = null,   // metros (para vehículos largos)
    
    // Información adicional
    val direction: String? = null,   // "norte", "sur", "ambos"
    val alternativeRoute: String? = null, // Ruta alternativa sugerida
    val notes: String? = null,
    
    // Ubicación administrativa
    val city: String? = null,
    val province: String? = null,
    val region: String,
    
    // Trazabilidad (OBLIGATORIO)
    val contributedBy: String,
    val source: String = "community", // community, official, import
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastConfirmedAt: Long? = null,
    val lastConfirmedBy: String? = null,
    
    // Estado
    val isVerified: Boolean = false,
    val isActive: Boolean = true,
    val reportCount: Int = 0
)

/**
 * Tipos de restricción
 */
object RestrictionTypes {
    const val HEIGHT = "height"      // Altura máxima
    const val WIDTH = "width"        // Ancho máximo
    const val WEIGHT = "weight"      // Peso máximo
    const val LENGTH = "length"      // Longitud máxima
    const val COMBINED = "combined"  // Varias restricciones
    const val TUNNEL = "tunnel"      // Túnel con restricciones
    const val BRIDGE = "bridge"      // Puente con restricciones
    const val UNDERPASS = "underpass" // Paso inferior
}

/**
 * Niveles de severidad para alertas
 */
object RestrictionSeverity {
    const val LOW = "low"           // Pasa la mayoría
    const val MEDIUM = "medium"     // Camiones grandes con cuidado
    const val HIGH = "high"         // Solo vehículos pequeños
    const val BLOCKED = "blocked"   // No pasa ningún camión/bus
}

/**
 * Extensión para verificar si un vehículo puede pasar
 */
fun Restriction.canPass(
    vehicleHeight: Double? = null,
    vehicleWidth: Double? = null,
    vehicleWeight: Double? = null,
    vehicleLength: Double? = null
): Boolean {
    // Si no hay datos del vehículo, no podemos verificar
    if (vehicleHeight == null && vehicleWidth == null && 
        vehicleWeight == null && vehicleLength == null) {
        return true // Asumimos que pasa si no hay datos
    }
    
    // Verificar cada restricción
    maxHeight?.let { max ->
        vehicleHeight?.let { vh ->
            if (vh > max) return false
        }
    }
    
    maxWidth?.let { max ->
        vehicleWidth?.let { vw ->
            if (vw > max) return false
        }
    }
    
    maxWeight?.let { max ->
        vehicleWeight?.let { vwt ->
            if (vwt > max) return false
        }
    }
    
    maxLength?.let { max ->
        vehicleLength?.let { vl ->
            if (vl > max) return false
        }
    }
    
    return true
}

/**
 * Extensión para obtener texto descriptivo de la restricción
 */
fun Restriction.getRestrictionText(): String {
    val parts = mutableListOf<String>()
    
    maxHeight?.let { parts.add("Altura máx: ${it}m") }
    maxWidth?.let { parts.add("Ancho máx: ${it}m") }
    maxWeight?.let { parts.add("Peso máx: ${it}t") }
    maxLength?.let { parts.add("Longitud máx: ${it}m") }
    
    return parts.joinToString(" • ")
}
