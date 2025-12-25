package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restrictions")
data class Restriction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val lat: Double,
    val lon: Double,
    
    val type: String,  // height, weight, width, length, dangerous_goods
    val value: Double, // El valor límite (ej: 3.5 metros)
    val unit: String = "m", // m, t
    
    val description: String? = null, // Ej: "Túnel bajo"
    
    val isVerified: Boolean = false,
    val reportCount: Int = 1, // Cuánta gente lo ha reportado
    
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Tipos de restricción
 */
object RestrictionTypes {
    const val MAX_HEIGHT = "height"
    const val MAX_WEIGHT = "weight"
    const val MAX_WIDTH = "width"
    const val MAX_LENGTH = "length"
    const val NO_TRUCKS = "no_trucks"
}
