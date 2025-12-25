package com.siempreabierto.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String, // Ej: "Ruta a Madrid"
    val originName: String,
    val destinationName: String,
    
    // Coordenadas
    val originLat: Double,
    val originLon: Double,
    val destLat: Double,
    val destLon: Double,
    
    // Geometría (La línea azul del mapa) guardada como JSON string
    val geometryJson: String, 
    val distanceMeters: Int,
    val durationSeconds: Int,
    
    val vehicleProfileUsed: String, // car, truck_large...
    
    val createdAt: Long = System.currentTimeMillis()
)
